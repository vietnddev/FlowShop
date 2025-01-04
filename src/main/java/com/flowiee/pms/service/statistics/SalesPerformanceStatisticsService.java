package com.flowiee.pms.service.statistics;

import com.flowiee.pms.model.statistics.OrderSalesChannelStatisticsModel;
import com.flowiee.pms.model.statistics.SalesPerformanceStatisticsModel;

import java.util.List;

public interface SalesPerformanceStatisticsService {
    List<SalesPerformanceStatisticsModel> getPerformanceEmployee();

    List<OrderSalesChannelStatisticsModel> getOrderBySalesChannel();

    Float getRateOrdersSoldOnOnlineChannels();
}