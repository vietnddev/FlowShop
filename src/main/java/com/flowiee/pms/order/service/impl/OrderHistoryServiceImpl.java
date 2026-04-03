package com.flowiee.pms.order.service.impl;

import com.flowiee.pms.order.entity.Order;
import com.flowiee.pms.order.entity.OrderDetail;
import com.flowiee.pms.order.entity.OrderHistory;
import com.flowiee.pms.order.repository.OrderHistoryRepository;
import com.flowiee.pms.order.service.OrderHistoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderHistoryServiceImpl implements OrderHistoryService {
    OrderHistoryRepository mvOrderHistoryRepository;

    @Override
    public List<OrderHistory> save(Map<String, Object[]> logChanges, String title, Long orderId, Long orderItemId) {
        List<OrderHistory> orderHistories = new ArrayList<>();
        for (Map.Entry<String, Object[]> entry : logChanges.entrySet()) {
            String field = entry.getKey();
            String oldValue = ObjectUtils.isNotEmpty(entry.getValue()[0]) ? entry.getValue()[0].toString() : " ";
            String newValue = ObjectUtils.isNotEmpty(entry.getValue()[1]) ? entry.getValue()[1].toString() : " ";

            OrderHistory orderHistorySaved = mvOrderHistoryRepository.save(OrderHistory.builder()
                    .order(orderId != null ? new Order(orderId) : null)
                    .orderDetail(orderItemId != null ? new OrderDetail(orderItemId) : null)
                    .title(title)
                    .field(field)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .build());

            orderHistories.add(orderHistorySaved);
        }
        return orderHistories;
    }
}