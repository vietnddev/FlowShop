package com.flowiee.pms.modules.system.service;

import com.flowiee.pms.modules.inventory.entity.ProductDetail;

public interface SendOperatorNotificationService {
    void notifyWarningLowStock(ProductDetail pProductDetail);
}