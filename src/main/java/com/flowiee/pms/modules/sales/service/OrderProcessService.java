package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.modules.sales.dto.OrderDTO;

public interface OrderProcessService {
    void cancelOrder(OrderDTO pOrder, String pReason);

    void completeOrder(OrderDTO pOrder);

    void returnOrder(OrderDTO pOrder);

    void refundOrder(Long pOrderId);
}