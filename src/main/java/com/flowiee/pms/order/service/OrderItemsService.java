package com.flowiee.pms.order.service;

import com.flowiee.pms.cart.entity.Items;
import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.order.entity.OrderDetail;
import com.flowiee.pms.order.dto.OrderDTO;

import java.util.List;

public interface OrderItemsService extends ICurdService<OrderDetail> {
    List<OrderDetail> findByOrderId(Long pOrderId);

    List<OrderDetail> save(OrderDTO orderDto, List<String> productVariantIds);

    List<OrderDetail> save(Long pCartId, Long pOrderId, List<Items> pItemsList);

    void updateReturnsStatus(long pItemId, boolean pIsReturned);
}