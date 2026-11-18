package com.flowiee.pms.product.service;

import com.flowiee.pms.product.enums.StockMovementType;
import com.flowiee.pms.product.enums.StockQuantityType;

public interface ProductStockMovementService {
    void createMovement(
            Long variantId,
            StockMovementType movementType,
            StockQuantityType quantityType,
            Integer quantityChange,
            String referenceType,
            Long referenceId,
            String note,
            Long createdBy
    );

    void createMovement(
            Long variantId,
            StockMovementType movementType,
            StockQuantityType quantityType,
            Integer quantityChange,
            String referenceType,
            Long referenceId,
            String note
    );
}