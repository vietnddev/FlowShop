package com.flowiee.pms.service.sales;

import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.model.dto.OrderDTO;

public interface OrderProcessService {
    void cancelOrder(OrderDTO pOrder, String pReason);

    void completeOrder(OrderDTO pOrder);

    void returnOrder(OrderDTO pOrder);

    void refundOrder(Long pOrderId);
}