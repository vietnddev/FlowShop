package com.flowiee.pms.modules.report.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RevenueStatisticsService {
    BigDecimal getDayRevenue(LocalDate pFromDate, LocalDate pToDate);

    BigDecimal getWeekRevenue();

    BigDecimal getMonthRevenue(Integer pFMonth, Integer pFYear, Integer pTMonth, Integer pTYear);

    void getRevenueOnEachProduct();
}