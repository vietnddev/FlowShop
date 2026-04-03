package com.flowiee.pms.order.service;

import com.flowiee.pms.order.entity.OrderHistory;

import java.util.List;
import java.util.Map;

public interface OrderHistoryService {
    List<OrderHistory> save(Map<String, Object[]> logChanges, String title, Long orderId, Long orderItemId);
}