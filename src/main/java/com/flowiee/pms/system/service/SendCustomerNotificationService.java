package com.flowiee.pms.system.service;

import com.flowiee.pms.order.entity.Order;

public interface SendCustomerNotificationService {
    void notifyOrderConfirmation(Order pOrderInfo, String pRecipient);
}