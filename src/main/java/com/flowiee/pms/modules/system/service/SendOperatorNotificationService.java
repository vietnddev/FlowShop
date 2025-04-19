package com.flowiee.pms.modules.system.service;

import com.flowiee.pms.modules.product.entity.ProductDetail;

public interface SendOperatorNotificationService {
    void notifyWarningLowStock(ProductDetail pProductDetail);
}