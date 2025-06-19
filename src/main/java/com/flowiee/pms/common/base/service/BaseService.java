package com.flowiee.pms.common.base.service;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.common.utils.DateTimeUtil;
import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.modules.sales.entity.Customer;
import com.flowiee.pms.modules.sales.entity.OrderCart;
import com.flowiee.pms.modules.sales.entity.VoucherTicket;
import com.flowiee.pms.modules.staff.entity.Account;
import com.flowiee.pms.modules.system.entity.SystemLog;
import com.flowiee.pms.common.model.Filter;
import com.flowiee.pms.common.exception.EntityNotFoundException;
import com.flowiee.pms.modules.system.repository.SystemLogRepository;
import com.flowiee.pms.common.security.UserSession;
import jakarta.persistence.EntityGraph;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BaseService<E, D, R extends BaseRepository<E, Long>> {
    @Autowired
    protected R mvEntityRepository;
    @Autowired
    protected EntityManager mvEntityManager;
    @Autowired
    private ModelMapper mvModelMapper;
    @Autowired
    private SystemLogRepository mvSystemLogRepository;
    @Autowired
    private UserSession mvUserSession;

    private Class<E> mvEntityClass;
    private Class<D> mvDtoClass;
    private boolean mvAutoAudit = false;

    public BaseService(R pEntityRepository) {
        this.mvEntityRepository = pEntityRepository;
    }

    public BaseService(Class<E> pEntityClass, Class<D> pDtoClass, R pEntityRepository) {
        this.mvEntityClass = pEntityClass;
        this.mvDtoClass = pDtoClass;
        this.mvEntityRepository = pEntityRepository;
    }

    public Optional<E> findById(Long id) {
        return mvEntityRepository.findById(id);
    }

    public E findEntById(Long pId, boolean throwException) {
        return mvEntityRepository.findById(pId)
                .orElseThrow(() -> throwException ?
                        new EntityNotFoundException(new Object[]{String.format("%s with Id %s", mvEntityClass.getSimpleName(), pId)}, null, null) :
                        null);
    }

    public D findDtoById(Long pId, boolean throwException) {
        return mvEntityRepository.findById(pId)
                .map(entity -> mvModelMapper.map(entity, mvDtoClass))
                .orElseThrow(() -> throwException ?
                        new EntityNotFoundException(new Object[]{String.format("%s with Id %s", mvEntityClass.getSimpleName(), pId)}, null, null) :
                        null);
    }

    public List<D> findByIds(List<Long> pIds) {
        if (CollectionUtils.isEmpty(pIds)) {
            return List.of();
        }
        return convertDTOs(mvEntityRepository.findAllById(pIds));
    }

    public List<D> findAll() {
        List<E> lvEntities = mvEntityRepository.findAll();
        return lvEntities.isEmpty() ? List.of() : convertDTOs(lvEntities);
    }

    public D save(D pDto) {
        E entity = mvModelMapper.map(pDto, mvEntityClass);
        E savedEntity = mvEntityRepository.save(entity);
        return mvModelMapper.map(savedEntity, mvDtoClass);
    }

    public D update(D pDto, Long pId) {
        if (!mvEntityRepository.existsById(pId)) {
            throw new EntityNotFoundException(new Object[]{String.format("%s with Id %s", mvEntityClass.getSimpleName(), pId)}, null, null);
        }
        E entity = mvModelMapper.map(pDto, mvEntityClass);
        E savedEntity = mvEntityRepository.save(entity);
        return mvModelMapper.map(savedEntity, mvDtoClass);
    }

    public String delete(Long pId) {
        if (!mvEntityRepository.existsById(pId)) {
            return mvEntityClass.getSimpleName() + " not found with Id: " + pId;
        }
        mvEntityRepository.deleteById(pId);
        if (mvAutoAudit) {
            mvSystemLogRepository.save(SystemLog.builder()
                    .module("AT-")
                    .function("D-")
                    .object(mvEntityClass.getSimpleName())
                    .mode("D")
                    .title("Delete entity")
                    .content("Delete " + mvEntityClass.getSimpleName() + " with ID is " + pId)
                    .contentChange("-")
                    .ip(mvUserSession.getUserPrincipal().getIp())
                    .account(new Account(mvUserSession.getUserPrincipal().getId()))
                    .build());
        }
        return "Entity with ID " + pId + " deleted successfully.";
    }

    public void setAutoAudit(boolean pAutoAudit) {
        this.mvAutoAudit = pAutoAudit;
    }

    public D convertDTO(E pInput) {
        if (pInput == null) {
            return null;
        }
        return mvModelMapper.map(pInput, mvDtoClass);
    }

    public List<D> convertDTOs(List<E> pInput) {
        if (CollectionUtils.isEmpty(pInput)) {
            return Collections.emptyList();
        }
        return pInput.stream()
                .map(this::convertDTO)
                .toList();
    }

    protected Pageable getPageable(int pageNum, int pageSize) {
        return getPageable(pageNum, pageSize, null);
    }

    protected Pageable getPageable(int pageNum, int pageSize, Sort sort) {
        if (pageNum >= 0 && pageSize > 0) {
            return sort != null ? PageRequest.of(pageNum, pageSize, sort)
                    : PageRequest.of(pageNum, pageSize);
        }
        return Pageable.unpaged();
    }

    public class QueryBuilder<T> {
        private final Class<T> entityClass;
        private final CriteriaBuilder cb;
        private final CriteriaQuery<T> query;
        private final Root<T> root;
        private final List<Predicate> predicates = new ArrayList<>();
        private final List<Order> orders = new ArrayList<>();
        private final Map<String, Join<?, ?>> joins = new HashMap<>();
        private final EntityManager entityManager;

        public QueryBuilder(Class<T> entityClass, EntityManager entityManager) {
            this.entityClass = entityClass;
            this.entityManager = entityManager;
            this.cb = entityManager.getCriteriaBuilder();
            this.query = cb.createQuery(entityClass);
            this.root = query.from(entityClass);
        }

        public CriteriaBuilder getCriteriaBuilder() {
            return this.cb;
        }

        public Root<T> getRoot() {
            return this.root;
        }

        public EntityManager getEntityManager() {
            return this.entityManager;
        }

        // Thêm điều kiện equal với path phức tạp (vd: "product.brand.id")
        public QueryBuilder<T> addEqual(String fieldPath, Object value) {
            if (value != null) {
                Path<Object> path = resolvePath(fieldPath);
                predicates.add(cb.equal(path, value));
            }
            return this;
        }

        // Thêm điều kiện like với path phức tạp
        public QueryBuilder<T> addLike(String fieldPath, String value) {
            if (value != null) {
                Path<String> path = resolvePath(fieldPath);
                predicates.add(cb.like(cb.lower(path), "%" + value.toLowerCase() + "%"));
            }
            return this;
        }

        // Thêm điều kiện like cho nhiều field
        public QueryBuilder<T> addLike(String value, String... fieldPaths) {
            if (value != null && fieldPaths != null && fieldPaths.length > 0) {
                List<Predicate> likePredicates = Arrays.stream(fieldPaths)
                        .map(fieldPath -> cb.like(cb.lower(resolvePath(fieldPath)), "%" + value.toLowerCase() + "%"))
                        .collect(Collectors.toList());

                predicates.add(cb.or(likePredicates.toArray(new Predicate[0])));
            }
            return this;
        }

        // Thêm điều kiện between
        public QueryBuilder<T> addBetween(String fieldPath, Comparable from, Comparable to) {
            if (from != null && to != null) {
                Path<? extends Comparable> path = resolvePath(fieldPath);
                predicates.add(cb.between(path, from, to));
            }
            return this;
        }

        // Thêm sắp xếp
        public QueryBuilder<T> addOrder(String fieldPath, boolean ascending) {
            Path<?> path = resolvePath(fieldPath);
            orders.add(ascending ? cb.asc(path) : cb.desc(path));
            return this;
        }

        // Thêm predicate tùy chỉnh
        public QueryBuilder<T> addPredicate(Function<CriteriaBuilder, Predicate> predicateFunction) {
            if (predicateFunction != null) {
                Predicate predicate = predicateFunction.apply(this.cb);
                predicates.add(predicate);
            }
            return this;
        }

        public QueryBuilder<T> fetch(String associationPath, JoinType joinType) {
            String[] parts = associationPath.split("\\.");
            From<?, ?> from = this.root;

            for (int i = 0; i < parts.length; i++) {
                if (i == parts.length - 1) {
                    from.fetch(parts[i], joinType);
                } else {
                    from = getOrCreateJoin(from, parts[i], joinType);
                }
            }
            return this;
        }

        // Build query
        public TypedQuery<T> build() {
            return build(Pageable.unpaged());
        }

        public TypedQuery<T> build(Pageable pageable) {
            query.where(predicates.toArray(new Predicate[0]));

            if (!orders.isEmpty()) {
                query.orderBy(orders);
            }

            TypedQuery<T> typedQuery = entityManager.createQuery(query);

            if (pageable != null && pageable.isPaged()) {
                typedQuery.setFirstResult((int) pageable.getOffset());
                typedQuery.setMaxResults(pageable.getPageSize());
            }

            return typedQuery;
        }

        // Build count query
        public Long buildCount() {
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<T> countRoot = countQuery.from(entityClass);

            List<Predicate> countPredicates = predicates.stream()
                    .map(p -> recreatePredicate(p, countRoot))
                    .collect(Collectors.toList());

            countQuery.select(cb.countDistinct(countRoot));
            countQuery.where(countPredicates.toArray(new Predicate[0]));

            return entityManager.createQuery(countQuery).getSingleResult();
        }

        // Helper methods
        private <X> Path<X> resolvePath(String path) {
            String[] parts = path.split("\\.");
            Path<X> currentPath = root.get(parts[0]);

            for (int i = 1; i < parts.length; i++) {
                currentPath = currentPath.get(parts[i]);
            }

            return currentPath;
        }

        private Join<?, ?> getOrCreateJoin(From<?, ?> from, String attribute, JoinType joinType) {
            String joinKey = from.getAlias() + "." + attribute;
            return joins.computeIfAbsent(joinKey, k -> from.join(attribute, joinType));
        }

        private Predicate recreatePredicate(Predicate original, Root<?> newRoot) {
            // Đơn giản hóa - trong thực tế cần phân tích predicate và tạo lại
            // Có thể sử dụng thư viện như ModelMapper hoặc viết logic phức tạp hơn
            return cb.conjunction();
        }

        public EntityGraph<T> createEntityGraph() {
            return this.entityManager.createEntityGraph(entityClass);
        }
    }

    // Phương thức khởi tạo query builder
    protected <T> QueryBuilder<T> createQueryBuilder(Class<T> entityClass) {
        return createQueryBuilder(entityClass, mvEntityManager);
    }

    protected <T> QueryBuilder<T> createQueryBuilder(Class<T> entityClass, EntityManager pEntityManager) {
        return new QueryBuilder<>(entityClass, pEntityManager);
    }

    protected <T> Specification<T> buildSpecification(List<Filter> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (Filter filter : filters) {
                switch (filter.getOperator()) {
                    case EQUALS:
                        predicates.add(criteriaBuilder.equal(root.get(filter.getField()), filter.getValue()));
                        break;
                    case LIKE:
                        predicates.add(criteriaBuilder.like(root.get(filter.getField()), "%" + filter.getValue() + "%"));
                        break;
                    case GREATER_THAN:
                        predicates.add(criteriaBuilder.greaterThan(root.get(filter.getField()), filter.getValue().toString()));
                        break;
                    case LESS_THAN:
                        predicates.add(criteriaBuilder.lessThan(root.get(filter.getField()), filter.getValue().toString()));
                        break;
                    default:
                        throw new UnsupportedOperationException("Operator not supported: " + filter.getOperator());
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public <E, D> Page<D> mapEntPageToDtoPage(Page<E> entityPage, Class<D> dtoClass) {
        List<D> dtoList = entityPage.getContent()
                .stream()
                .map(entity -> mvModelMapper.map(entity, dtoClass))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Data
    protected class VldModel {
        private Account salesAssistant;
        private Category paymentMethod;
        private Category brand;
        private Category color;
        private Category fabricType;
        private Category size;
        private Category salesChannel;
        private Category unit;
        private Category productType;
        private Customer customer;
        private OrderCart orderCart;
        private VoucherTicket voucherTicket;
    }
}