package com.flowiee.pms.common.base.service;

import com.flowiee.pms.common.utils.DateTimeUtil;
import com.flowiee.pms.modules.category.entity.Category;
import com.flowiee.pms.modules.sales.entity.Customer;
import com.flowiee.pms.modules.sales.entity.OrderCart;
import com.flowiee.pms.modules.promotion.entity.VoucherTicket;
import com.flowiee.pms.modules.user.entity.Account;
import com.flowiee.pms.common.security.UserSession;
import com.flowiee.pms.modules.log.service.SystemLogService;
import com.flowiee.pms.common.enumeration.FilterDate;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

@Component
public class BaseService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected SystemLogService systemLogService;
    @Autowired
    protected EntityManager mvEntityManager;
    @Autowired
    protected UserSession mvUserSession;

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

    public LocalDateTime[] getFromDateToDate(FilterDate pFilterDate) {
        LocalDateTime lvFromDate = null;
        LocalDateTime lvEndDate = null;

        LocalDate today = LocalDate.now();
        LocalDateTime startOfToDay = today.atTime(LocalTime.MIN);
        LocalDateTime endOfToDay = today.atTime(LocalTime.MAX);

        YearMonth yearMonth = YearMonth.of(today.getYear(), today.getMonthValue());
        LocalDateTime startDayOfMonth = yearMonth.atDay(1).atTime(LocalTime.MIN);
        LocalDateTime endDayOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        switch (pFilterDate) {
            case ToDay:
                lvFromDate = startOfToDay;
                lvEndDate = endOfToDay;
                break;
            case PreviousDay:
                lvFromDate = startOfToDay.minusDays(1);
                lvEndDate = endOfToDay.minusDays(1);
                break;
            case SevenDaysAgo:
                lvFromDate = startOfToDay.minusDays(7);
                lvEndDate = endOfToDay;
                break;
            case ThisMonth:
                lvFromDate = startDayOfMonth;
                lvEndDate = endDayOfMonth;
                break;
            case PreviousMonth:
                lvFromDate = startDayOfMonth.minusMonths(1);
                lvEndDate = endDayOfMonth.minusMonths(1);
        }

        return new LocalDateTime[] {lvFromDate, lvEndDate};
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

    @Data
    protected class VldModel {
        private Account       salesAssistant;
        private Category      paymentMethod;
        private Category      brand;
        private Category      color;
        private Category      fabricType;
        private Category      size;
        private Category      salesChannel;
        private Category      unit;
        private Category      productType;
        private Customer      customer;
        private OrderCart     orderCart;
        private VoucherTicket voucherTicket;
    }
}