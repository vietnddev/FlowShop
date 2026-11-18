package com.flowiee.pms.product.service.impl;

import com.flowiee.pms.product.entity.ProductDetail;
import com.flowiee.pms.product.entity.ProductInventory;
import com.flowiee.pms.product.entity.ProductStockMovement;
import com.flowiee.pms.product.enums.StockMovementType;
import com.flowiee.pms.product.enums.StockQuantityType;
import com.flowiee.pms.product.repository.ProductDetailRepository;
import com.flowiee.pms.product.repository.ProductStockMovementRepository;
import com.flowiee.pms.product.service.ProductInventoryService;
import com.flowiee.pms.product.service.ProductStockMovementService;
import com.flowiee.pms.shared.exception.BadRequestException;
import com.flowiee.pms.shared.util.SecurityUtils;
import com.flowiee.pms.shared.util.SysConfigUtils;
import com.flowiee.pms.system.enums.ConfigCode;
import com.flowiee.pms.system.service.SendOperatorNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductStockMovementServiceImpl implements ProductStockMovementService {
    private final SendOperatorNotificationService sendOperatorNotificationService;
    private final ProductStockMovementRepository movementRepository;
    private final ProductDetailRepository productVariantRepository;
    private final ProductInventoryService inventoryService;

    @Override
    @Transactional
    public void createMovement(Long variantId, StockMovementType movementType, StockQuantityType quantityType, Integer quantityChange, String referenceType, Long referenceId, String note) {
        this.createMovement(variantId, movementType, quantityType, quantityChange, referenceType, referenceId, note, SecurityUtils.getCurrentUser().getId());
    }

    @Override
    @Transactional
    public void createMovement(Long variantId, StockMovementType movementType, StockQuantityType stockQuantityType,
                                               Integer quantityChange, String referenceType, Long referenceId, String note, Long createdBy) {
        // Validate input
        if (variantId == null) {
            throw new IllegalArgumentException("Variant ID must not be null!");
        }
        if (movementType == null || stockQuantityType == null) {
            throw new IllegalArgumentException("Movement type and quantity type are required!");
        }
        if (quantityChange == null || quantityChange == 0) {
            throw new IllegalArgumentException("Quantity change must not be zero!");
        }

        // Get inventory with database lock
        ProductDetail productVariant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new BadRequestException("Product Variant not found!"));
        ProductInventory inventory = productVariant.getProductInventory();

        validateMovement(movementType, quantityChange);

        // Get current quantity
        int quantityBefore = switch (stockQuantityType) {
            case ON_HAND -> inventory.getOnHandQuantity();
            case RESERVED -> inventory.getReservedQuantity();
            case DEFECTIVE -> inventory.getDefectiveQuantity();
            case REORDER_LEVEL -> inventory.getReorderLevel();
        };

        // Calculate new quantity
        int quantityAfter = quantityBefore + quantityChange;

        // Validate inventory business rules
        int onHand = inventory.getOnHandQuantity();
        int reserved = inventory.getReservedQuantity();
        int defective = inventory.getDefectiveQuantity();
        int reorderLevel = inventory.getReorderLevel();

        switch (stockQuantityType) {
            case ON_HAND:
                // Cannot reduce on-hand below reserved + defective
                if (quantityAfter < reserved + defective) {
                    throw new IllegalArgumentException("On-hand quantity cannot be less than reserved + defective quantity!");
                }

                if (reorderLevel <= quantityAfter) {
                    if (SysConfigUtils.isYesOption(ConfigCode.lowStockAlert)) {
                        sendOperatorNotificationService.notifyWarningLowStock(productVariant);
                    }
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

        // Update the corresponding inventory field
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
            case REORDER_LEVEL:
                inventory.setReorderLevel(quantityAfter);
                break;
        }

        // Create stock movement history
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

        // Save movement
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

                //        ProductDetail lvProductDetail = super.findEntById(pProductVariantId, true);
//        Integer lvLowStockThreshold = lvProductDetail.getLowStockThreshold();
//        int lvCurrentQuantity = lvProductDetail.getStorageQty();
//        try {
//            if ("I".equals(pUpdateType)) {
//                mvEntityRepository.updateQuantityIncrease(pQuantity, pProductVariantId);
//            } else if ("D".equals(pUpdateType)) {
//                if (lvCurrentQuantity < pQuantity)
//                    throw new BadRequestException("Hàng tồn kho không đủ số lượng!");
//
//                lvProductDetail.setStorageQty(lvCurrentQuantity - pQuantity);
//                lvProductDetail.setSoldQty(lvProductDetail.getSoldQty() + pQuantity);
//                ProductDetail productDetailUpdated = mvEntityRepository.save(lvProductDetail);
//
//                if (lvLowStockThreshold != null && productDetailUpdated.getStorageQty() <= lvLowStockThreshold) {
//                    if (SysConfigUtils.isYesOption(ConfigCode.lowStockAlert)) {
//                        mvSendOperatorNotificationService.notifyWarningLowStock(productDetailUpdated);
//                    }
//                }
//
//                //Hết hàng
//                if (productDetailUpdated.getAvailableSalesQty() == 0) {
//                    productDetailUpdated.setStatus(ProductStatus.OOS);
//                    productDetailUpdated.setOutOfStockDate(LocalDateTime.now());
//                    mvEntityRepository.save(productDetailUpdated);
//                }
//            }
//            mvSystemLogService.writeLogUpdate(ACTION.PRO_PRD_U, MasterObject.ProductVariant, "Cập nhật số lượng sản phẩm", "productVariantId = " + pProductVariantId);
//        } catch (RuntimeException ex) {
//            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "product quantity"), ex);
//        }

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