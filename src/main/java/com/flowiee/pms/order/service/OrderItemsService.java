package com.flowiee.pms.order.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.order.entity.OrderDetail;
import com.flowiee.pms.order.dto.OrderDTO;

import java.util.List;

public interface OrderItemsService extends ICurdService<OrderDetail> {
    List<OrderDetail> save(OrderDTO orderDto, List<String> productVariantIds);

    List<OrderDetail> save(Long pCartId, Long pOrderId);
}