package com.flowiee.pms.report.service;

import com.flowiee.pms.report.model.OrderSalesChannelStatisticsModel;
import com.flowiee.pms.report.model.SalesPerformanceStatisticsModel;

import java.util.List;

public interface SalesPerformanceStatisticsService {
    List<SalesPerformanceStatisticsModel> getPerformanceEmployee();

    List<OrderSalesChannelStatisticsModel> getOrderBySalesChannel();

    Float getRateOrdersSoldOnOnlineChannels();
}