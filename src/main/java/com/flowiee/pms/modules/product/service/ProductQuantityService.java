package com.flowiee.pms.modules.product.service;

public interface ProductQuantityService {
    void updateProductVariantQuantityIncrease(Integer pQuantity, Long pProductVariantId);

    void updateProductVariantQuantityDecrease(Integer pQuantity, Long pProductVariantId);
}