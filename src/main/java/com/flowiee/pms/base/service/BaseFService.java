package com.flowiee.pms.base.service;

import com.flowiee.pms.base.BaseRepository;
import com.flowiee.pms.model.Filter;
import com.flowiee.pms.exception.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public abstract class BaseFService<E, D, R extends BaseRepository<E, Long>> {
    @Autowired
    protected R entityRepository;
    @Autowired
    private ModelMapper modelMapper;
    private Class<E> entityClass;
    private Class<D> dtoClass;

    public Optional<E> findById(Long id) {
        return entityRepository.findById(id);
    }

    public D findById(Long pId, boolean throwException) {
        return entityRepository.findById(pId)
                .map(entity -> modelMapper.map(entity, dtoClass))
                .orElseThrow(() -> throwException ?
                        new EntityNotFoundException(new Object[]{String.format("%s with Id %s", entityClass.getSimpleName(), pId)}, null, null) :
                        null);
    }

    public D save(D pDto) {
        E entity = modelMapper.map(pDto, entityClass);
        E savedEntity = entityRepository.save(entity);
        return modelMapper.map(savedEntity, dtoClass);
    }

    public D update(D pDto, Long pId) {
        if (!entityRepository.existsById(pId)) {
            throw new EntityNotFoundException(new Object[]{String.format("%s with Id %s", entityClass.getSimpleName(), pId)}, null, null);
        }
        E entity = modelMapper.map(pDto, entityClass);
        E savedEntity = entityRepository.save(entity);
        return modelMapper.map(savedEntity, dtoClass);
    }

    public String delete(Long pId) {
        if (!entityRepository.existsById(pId)) {
            return entityClass.getSimpleName() + " not found with Id: " + pId;
        }
        entityRepository.deleteById(pId);
        return "Entity with ID " + pId + " deleted successfully.";
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
                .map(entity -> modelMapper.map(entity, dtoClass))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }
}