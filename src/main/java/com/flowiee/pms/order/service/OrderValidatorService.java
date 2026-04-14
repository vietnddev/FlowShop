package com.flowiee.pms.order.service;

import com.flowiee.pms.order.entity.Order;
import com.flowiee.pms.order.enums.OrderStatus;
import com.flowiee.pms.order.model.ChangeOrderStatusReq;
import com.flowiee.pms.order.model.CreateOrderReq;
import com.flowiee.pms.order.model.UpdateOrderReq;

public interface OrderValidatorService {
    void validateCreateOrder(CreateOrderReq pRequest);

    void validateUpdateOrder(UpdateOrderReq request, Long pOrderId);

    void validateStatusTransition(Order pOrder, OrderStatus pOldStatus, OrderStatus pNewStatus, ChangeOrderStatusReq pRequest);
}