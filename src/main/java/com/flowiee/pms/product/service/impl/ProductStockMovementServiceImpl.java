package com.flowiee.pms.product.service.impl;

import com.flowiee.pms.product.entity.ProductInventory;
import com.flowiee.pms.product.entity.ProductStockMovement;
import com.flowiee.pms.product.enums.StockMovementType;
import com.flowiee.pms.product.enums.StockQuantityType;
import com.flowiee.pms.product.repository.ProductStockMovementRepository;
import com.flowiee.pms.product.service.ProductInventoryService;
import com.flowiee.pms.product.service.ProductStockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductStockMovementServiceImpl implements ProductStockMovementService {
    private final ProductInventoryService inventoryService;
    private final ProductStockMovementRepository movementRepository;

    @Override
    @Transactional
    public void createMovement(Long variantId, StockMovementType movementType, StockQuantityType stockQuantityType,
                                               Integer quantityChange, String referenceType, Long referenceId, String note, Long createdBy) {
        // 1. Validate input
        if (variantId == null) {
            throw new IllegalArgumentException("Variant ID must not be null!");
        }
        if (movementType == null || stockQuantityType == null) {
            throw new IllegalArgumentException("Movement type and quantity type are required!");
        }
        if (quantityChange == null || quantityChange == 0) {
            throw new IllegalArgumentException("Quantity change must not be zero!");
        }

        validateMovement(movementType, quantityChange);

        // 2. Get inventory with database lock
        ProductInventory inventory = inventoryService.getByVariantId(variantId);

        // 3. Get current quantity
        int quantityBefore = switch (stockQuantityType) {
            case ON_HAND -> inventory.getOnHandQuantity();
            case RESERVED -> inventory.getReservedQuantity();
            case DEFECTIVE -> inventory.getDefectiveQuantity();
        };

        // 4. Calculate new quantity
        int quantityAfter = quantityBefore + quantityChange;
        if (quantityAfter < 0) {
            throw new IllegalArgumentException(
                    "Inventory quantity cannot be negative!"
            );
        }

        // 5. Validate inventory business rules
        int onHand = inventory.getOnHandQuantity();
        int reserved = inventory.getReservedQuantity();
        int defective = inventory.getDefectiveQuantity();

        switch (stockQuantityType) {
            case ON_HAND:
                // Cannot reduce on-hand below reserved + defective
                if (quantityAfter < reserved + defective) {
                    throw new IllegalArgumentException("On-hand quantity cannot be less than reserved + defective quantity!");
                }
                break;
            case RESERVED:
                // Reserved quantity cannot exceed sellable stock
                if (quantityAfter + defective > onHand) {
                    throw new IllegalArgumentException("Reserved quantity exceeds available stock!");
                }
                break;

            case DEFECTIVE:
                // Defective quantity cannot exceed physical stock
                if (reserved + quantityAfter > onHand) {
                    throw new IllegalArgumentException("Defective quantity exceeds available stock!");
                }
                break;
        }

        // 6. Update the corresponding inventory field
        switch (stockQuantityType) {
            case ON_HAND:
                inventory.setOnHandQuantity(quantityAfter);
                break;
            case RESERVED:
                inventory.setReservedQuantity(quantityAfter);
                break;
            case DEFECTIVE:
                inventory.setDefectiveQuantity(quantityAfter);
                break;
        }

        // 7. Create stock movement history
        ProductStockMovement movement = new ProductStockMovement();

        movement.setProductInventory(inventory);
        movement.setMovementType(movementType);
        movement.setQuantityType(stockQuantityType);

        movement.setQuantityBefore(quantityBefore);
        movement.setQuantityChange(quantityChange);
        movement.setQuantityAfter(quantityAfter);

        movement.setReferenceType(referenceType);
        movement.setReferenceId(referenceId);
        movement.setNote(note);

        // 8. Save movement
        movementRepository.save(movement);
    }

    private void validateMovement(StockMovementType movementType, Integer quantityChange) {
        if (movementType == null) {
            throw new IllegalArgumentException("Movement type is required!");
        }

        switch (movementType) {
            case OPENING_STOCK:
                break;
            case PURCHASE:
                break;
            case CUSTOMER_RETURN:
                if (quantityChange < 0) {
                    throw new IllegalArgumentException(movementType + " must have positive quantity!");
                }
                break;
            case SALE:
            case DAMAGE:
                if (quantityChange > 0) {
                    throw new IllegalArgumentException(movementType + " must have negative quantity!");
                }
                break;
            case ADJUSTMENT:
                // Can be positive or negative
                break;
        }
    }
}