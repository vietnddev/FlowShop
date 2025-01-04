package com.flowiee.pms.service.sales;

import com.flowiee.pms.entity.sales.Order;

public interface OrderProcessService {
    void cancelOrder(Order pOrder, String pReason);

    void completeOrder(Order pOrder);

    void returnOrder(Order pOrder);

    void refundOrder(Long pOrderId);
}