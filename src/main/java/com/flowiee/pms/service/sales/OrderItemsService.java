package com.flowiee.pms.service.sales;

import com.flowiee.pms.entity.sales.Items;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.entity.sales.OrderDetail;
import com.flowiee.pms.model.dto.OrderDTO;

import java.util.List;

public interface OrderItemsService extends BaseCurdService<OrderDetail> {
    List<OrderDetail> findByOrderId(Long pOrderId);

    List<OrderDetail> save(OrderDTO orderDto, List<String> productVariantIds);

    List<OrderDetail> save(Long pCartId, Long pOrderId, List<Items> pItemsList);
}