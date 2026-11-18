package com.flowiee.pms.product.service;

import com.flowiee.pms.product.entity.ProductDetail;
import com.flowiee.pms.product.entity.ProductInventory;

public interface ProductInventoryService {
    ProductInventory initializeInventory(ProductDetail variant);

    ProductInventory getByVariantId(Long variantId);
}