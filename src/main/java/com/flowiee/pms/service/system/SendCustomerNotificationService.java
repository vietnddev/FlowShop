package com.flowiee.pms.service.system;

import com.flowiee.pms.entity.sales.Order;

public interface SendCustomerNotificationService {
    void notifyOrderConfirmation(Order pOrderInfo, String pRecipient);
}