package com.flowiee.pms.modules.sales.service;

public interface OrderStatisticsService {
    Double findRevenueToday();

    Double findRevenueThisMonth();
}