package com.flowiee.pms.modules.system.service;

import com.flowiee.pms.modules.sales.entity.Order;

public interface SendCustomerNotificationService {
    void notifyOrderConfirmation(Order pOrderInfo, String pRecipient);
}