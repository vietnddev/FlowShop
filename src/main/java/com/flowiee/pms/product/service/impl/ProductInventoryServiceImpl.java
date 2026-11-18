package com.flowiee.pms.product.service.impl;

import com.flowiee.pms.product.entity.ProductDetail;
import com.flowiee.pms.product.entity.ProductInventory;
import com.flowiee.pms.product.repository.ProductInventoryRepository;
import com.flowiee.pms.product.service.ProductInventoryService;
import com.flowiee.pms.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductInventoryServiceImpl implements ProductInventoryService {
    private final ProductInventoryRepository productInventoryRepository;

    @Override
    public ProductInventory initializeInventory(ProductDetail variant) {
        log.info("initializeInventory for this variantId is {}", variant.getId());
        if (productInventoryRepository.existsByVariantId(variant.getId())) {
            log.warn("Product Inventory already exists for this variant with id {}", variant.getId());
            return getByVariantId(variant.getId());
        }

        ProductInventory inventory = new ProductInventory();
        inventory.setVariant(variant);
        inventory.setOnHandQuantity(0);
        inventory.setReservedQuantity(0);
        inventory.setDefectiveQuantity(0);
        inventory.setReorderLevel(0);

        variant.setProductInventory(inventory);

        return productInventoryRepository.save(inventory);
    }

    @Override
    public ProductInventory getByVariantId(Long variantId) {
        return productInventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Inventory not found for variant ID: " + variantId
                ));
    }
}