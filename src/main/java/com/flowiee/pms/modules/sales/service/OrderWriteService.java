package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.modules.sales.dto.OrderDTO;
import com.flowiee.pms.modules.sales.model.CreateOrderReq;
import com.flowiee.pms.modules.sales.model.UpdateOrderReq;

public interface OrderWriteService {
    OrderDTO createOrder(CreateOrderReq pRequest);

    OrderDTO updateOrder(UpdateOrderReq pRequest, Long pOrderId);

    String deleteOrder(Long pOrderId);
}