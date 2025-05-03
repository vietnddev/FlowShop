package com.flowiee.pms.common.base.service;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.common.utils.DateTimeUtil;
import com.flowiee.pms.modules.category.entity.Category;
import com.flowiee.pms.modules.sales.entity.Customer;
import com.flowiee.pms.modules.sales.entity.OrderCart;
import com.flowiee.pms.modules.promotion.entity.VoucherTicket;
import com.flowiee.pms.modules.user.entity.Account;
import com.flowiee.pms.modules.log.entity.SystemLog;
import com.flowiee.pms.common.model.Filter;
import com.flowiee.pms.common.exception.EntityNotFoundException;
import com.flowiee.pms.modules.log.repository.SystemLogRepository;
import com.flowiee.pms.common.security.UserSession;
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
import java.util.stream.Collectors;

@Service
public abstract class BaseGService<E, D, R extends BaseRepository<E, Long>> {
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

    public BaseGService(R pEntityRepository) {
        this.mvEntityRepository = pEntityRepository;
    }

    public BaseGService(Class<E> pEntityClass, Class<D> pDtoClass, R pEntityRepository) {
        this.mvEntityClass = pEntityClass;
        this.mvDtoClass = pDtoClass;
        this.mvEntityRepository = pEntityRepository;
    }

    public Optional<E> findById(Long id) {
        return mvEntityRepository.findById(id);
    }

    public D findById(Long pId, boolean throwException) {
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
                .map(entity -> mvModelMapper.map(entity, mvDtoClass))
                .toList();
    }

    protected Pageable getPageable(int pageNum, int pageSize) {
        return getPageable(pageNum, pageSize, null);
    }

    protected Pageable getPageable(int pageNum, int pageSize, Sort sort) {
        if (pageSize >= 0 && pageNum >= 0) {
            if (sort == null) {
                return PageRequest.of(pageNum, pageSize);
            }
            return PageRequest.of(pageNum, pageSize, sort);
        }
        return Pageable.unpaged();
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

    public <E, D> Page<D> mapEntityPageToDtoPage(Page<E> entityPage, Class<D> dtoClass) {
        List<D> dtoList = entityPage.getContent()
                .stream()
                .map(entity -> mvModelMapper.map(entity, dtoClass))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    protected <T> void addEqualCondition(
            CriteriaBuilder cb,
            List<Predicate> predicates,
            Path<T> path,
            T value) {
        if (value != null) {
            predicates.add(cb.equal(path, value));
        }
    }

    protected void addLikeCondition(
            CriteriaBuilder cb,
            List<Predicate> predicates,
            String value,
            Path<String>... fields) {
        if (value != null) {
            List<Predicate> likePredicates = Arrays.stream(fields)
                    .map(field -> cb.like(cb.lower(field), "%" + value.toLowerCase() + "%"))
                    .toList();
            predicates.add(cb.or(likePredicates.toArray(new Predicate[0])));
        }
    }

    protected <T extends Comparable<? super T>> void addBetweenCondition(
            CriteriaBuilder cb,
            List<Predicate> predicates,
            Expression<?> field,
            String functionName, // Tên hàm SQL
            Class<T> functionResultType, // Kiểu kết quả trả về từ hàm SQL
            T from,
            T to) {
        if (from != null && to != null) {
            Expression<T> functionExpression = cb.function(functionName, functionResultType, field);
            predicates.add(cb.between(functionExpression, from, to));
        }
    }

    protected <T> TypedQuery<Long> initCriteriaCountQuery(CriteriaBuilder lvCriteriaBuilder,
                                                          List<Predicate> predicates,
                                                          Class<T> entityClass) {
        CriteriaQuery<Long> countQuery = lvCriteriaBuilder.createQuery(Long.class);
        Root<T> countRoot = countQuery.from(entityClass);
        // Copy các điều kiện từ danh sách ban đầu
        if (predicates != null && !predicates.isEmpty()) {
            countQuery.where(predicates.toArray(new Predicate[0]));
        }
        // Chọn count distinct
        countQuery.select(lvCriteriaBuilder.countDistinct(countRoot));
        return mvEntityManager.createQuery(countQuery);
    }

    protected <T> TypedQuery<T> initCriteriaQuery(CriteriaBuilder pCriteriaBuilder,
                                                  CriteriaQuery<T> pCriteriaQuery,
                                                  Root<T> pRoot,
                                                  List<Predicate> pPredicates,
                                                  Pageable pPageable) {
        pCriteriaQuery.where(pPredicates.toArray(new Predicate[0]));
        pCriteriaQuery.distinct(true);

        if (pPageable.getSort().isSorted()) {
            List<jakarta.persistence.criteria.Order> orders = pPageable.getSort().stream()
                    .map(sortOrder  -> {
                        if (sortOrder.isAscending()) {
                            return pCriteriaBuilder.asc(pRoot.get(sortOrder.getProperty()));
                        } else {
                            return pCriteriaBuilder.desc(pRoot.get(sortOrder.getProperty()));
                        }
                    })
                    .toList();
            pCriteriaQuery.orderBy(orders);
        }

        TypedQuery<T> lvTypedQuery = mvEntityManager.createQuery(pCriteriaQuery);
        if (pPageable.isPaged()) {
            lvTypedQuery.setFirstResult((int) pPageable.getOffset());
            lvTypedQuery.setMaxResults(pPageable.getPageSize());
        }

        return lvTypedQuery;
    }

    protected LocalDateTime getFilterStartTime(LocalDateTime pTime) {
        if (pTime != null) {
            return pTime;
        }
        return DateTimeUtil.MIN_TIME;
    }

    protected LocalDateTime getFilterEndTime(LocalDateTime pTime) {
        if (pTime != null) {
            return pTime;
        }
        return DateTimeUtil.MAX_TIME;
    }

    public LocalDateTime[] getFromDateToDate(LocalDateTime pFromDate, LocalDateTime pToDate, String pFilterDate) {
        LocalDateTime lvStartTime = null;
        LocalDateTime lvEndTime = null;

        LocalDate today = LocalDate.now();
        LocalDateTime startOfToDay = today.atTime(LocalTime.MIN);
        LocalDateTime endOfToDay = today.atTime(LocalTime.MAX);

        YearMonth yearMonth = YearMonth.of(today.getYear(), today.getMonthValue());
        LocalDateTime startDayOfMonth = yearMonth.atDay(1).atTime(LocalTime.MIN);
        LocalDateTime endDayOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        switch (pFilterDate) {
            case "T0": //Today
                pFromDate = startOfToDay;
                pToDate = endOfToDay;
                break;
            case "T-1": //Previous day
                pFromDate = startOfToDay.minusDays(1);
                pToDate = endOfToDay.minusDays(1);
                break;
            case "T-7": //7 days ago
                pFromDate = startOfToDay.minusDays(7);
                pToDate = endOfToDay;
                break;
            case "M0": //This month
                pFromDate = startDayOfMonth;
                pToDate = endDayOfMonth;
                break;
            case "M-1": //Previous month
                pFromDate = startDayOfMonth.minusMonths(1);
                pToDate = endDayOfMonth.minusMonths(1);
        }

        lvStartTime = pFromDate;
        lvEndTime = pToDate;

        return new LocalDateTime[] {lvStartTime, lvEndTime};
    }

    @Data
    protected class VldModel {
        private Account salesAssistant;
        private Category paymentMethod;
        private Category      brand;
        private Category      color;
        private Category      fabricType;
        private Category      size;
        private Category      salesChannel;
        private Category      unit;
        private Category      productType;
        private Customer customer;
        private OrderCart orderCart;
        private VoucherTicket voucherTicket;
    }
}