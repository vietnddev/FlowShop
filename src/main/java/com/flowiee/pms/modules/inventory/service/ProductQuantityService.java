package com.flowiee.pms.modules.inventory.service;

public interface ProductQuantityService {
    void updateProductVariantQuantityIncrease(Integer pQuantity, Long pProductVariantId);

    void updateProductVariantQuantityDecrease(Integer pQuantity, Long pProductVariantId);
}