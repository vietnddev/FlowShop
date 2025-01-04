package com.flowiee.pms.service.sales;

import com.flowiee.pms.model.dto.OrderDTO;
import com.flowiee.pms.model.payload.CreateOrderReq;
import com.flowiee.pms.model.payload.UpdateOrderReq;

public interface OrderWriteService {
    OrderDTO createOrder(CreateOrderReq pRequest);

    OrderDTO updateOrder(UpdateOrderReq pRequest, Long pOrderId);

    String deleteOrder(Long pOrderId);
}