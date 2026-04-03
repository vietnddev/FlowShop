package com.flowiee.pms.system.service;

import com.flowiee.pms.product.entity.ProductDetail;

public interface SendOperatorNotificationService {
    void notifyWarningLowStock(ProductDetail pProductDetail);
}