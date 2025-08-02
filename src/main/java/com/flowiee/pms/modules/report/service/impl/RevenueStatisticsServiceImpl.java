package com.flowiee.pms.modules.report.service.impl;

import com.flowiee.pms.common.utils.DateTimeUtil;
import com.flowiee.pms.modules.report.service.RevenueStatisticsService;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.sales.repository.OrderRepository;
import com.flowiee.pms.common.utils.OrderUtils;
import com.flowiee.pms.common.enumeration.FilterDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueStatisticsServiceImpl implements RevenueStatisticsService {
    private final OrderRepository orderRepository;

    @Override
    public BigDecimal getDayRevenue(LocalDate pFromDate, LocalDate pToDate) {
        LocalDateTime[] lvDate;
        if (pFromDate == null || pFromDate == null) {
             lvDate = DateTimeUtil.getFromDateToDate(FilterDate.getByCode("T0"));//Today
        } else {
            lvDate = new LocalDateTime[] {pFromDate.atTime(LocalTime.MIN), pToDate.atTime(LocalTime.MAX)};
        }
        List<Order> lvOrderList = orderRepository.findBySuccessfulDeliveryTime(lvDate[0], lvDate[1]);
        BigDecimal lvRevenue = OrderUtils.calAmount(lvOrderList);
        return lvRevenue;
    }

    @Override
    public BigDecimal getWeekRevenue() {
        LocalDateTime[] lvDate = DateTimeUtil.getFromDateToDate(FilterDate.getByCode("T-7"));
        List<Order> lvOrderList = orderRepository.findBySuccessfulDeliveryTime(lvDate[0], lvDate[1]);
        BigDecimal lvRevenue = OrderUtils.calAmount(lvOrderList);
        return lvRevenue;
    }

    @Override
    public BigDecimal getMonthRevenue(Integer pFMonth, Integer pFYear, Integer pTMonth, Integer pTYear) {
        LocalDateTime[] lvDate;
        if (pFMonth == null || pFYear == null || pTMonth == null || pTYear == null) {
            lvDate = DateTimeUtil.getFromDateToDate(FilterDate.getByCode("M0"));//This month
        } else {
            lvDate = new LocalDateTime[] {
                    YearMonth.of(pFYear, pFMonth).atDay(1).atStartOfDay(),
                    YearMonth.of(pTYear, pTMonth).atEndOfMonth().atTime(23, 59, 59)
            };
        }
        List<Order> lvOrderList = orderRepository.findBySuccessfulDeliveryTime(lvDate[0], lvDate[1]);
        BigDecimal lvRevenue = OrderUtils.calAmount(lvOrderList);
        return lvRevenue;
    }

    @Override
    public void getRevenueOnEachProduct() {

    }
}