package com.flowiee.pms.service.system;

import com.flowiee.pms.entity.product.ProductDetail;

public interface SendOperatorNotificationService {
    void notifyWarningLowStock(ProductDetail pProductDetail);
}