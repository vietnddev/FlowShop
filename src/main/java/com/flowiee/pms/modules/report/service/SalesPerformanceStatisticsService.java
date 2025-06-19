package com.flowiee.pms.modules.report.service;

import com.flowiee.pms.modules.report.model.OrderSalesChannelStatisticsModel;
import com.flowiee.pms.modules.report.model.SalesPerformanceStatisticsModel;

import java.util.List;

public interface SalesPerformanceStatisticsService {
    List<SalesPerformanceStatisticsModel> getPerformanceEmployee();

    List<OrderSalesChannelStatisticsModel> getOrderBySalesChannel();

    Float getRateOrdersSoldOnOnlineChannels();
}