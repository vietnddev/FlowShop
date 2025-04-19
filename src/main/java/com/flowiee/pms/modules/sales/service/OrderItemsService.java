package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.modules.sales.entity.Items;
import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.sales.entity.OrderDetail;
import com.flowiee.pms.modules.sales.dto.OrderDTO;

import java.util.List;

public interface OrderItemsService extends BaseCurdService<OrderDetail> {
    List<OrderDetail> findByOrderId(Long pOrderId);

    List<OrderDetail> save(OrderDTO orderDto, List<String> productVariantIds);

    List<OrderDetail> save(Long pCartId, Long pOrderId, List<Items> pItemsList);
}