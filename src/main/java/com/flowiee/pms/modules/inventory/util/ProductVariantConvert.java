package com.flowiee.pms.modules.inventory.util;

import com.flowiee.pms.modules.inventory.entity.Product;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import com.flowiee.pms.modules.system.entity.Category;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductVariantConvert {
    public static List<ProductVariantDTO> entitiesToDTOs(List<ProductDetail> pInput) {
        if (CollectionUtils.isEmpty(pInput)) {
            return new ArrayList<>();
        }
        return pInput.stream()
                .map(ProductVariantConvert::toDto)
                .toList();
    }

    public static ProductVariantDTO toDto(ProductDetail pInput) {
        if (ObjectUtils.isEmpty(pInput)) {
            return new ProductVariantDTO();
        }

        ProductVariantDTO dto = new ProductVariantDTO();
        dto.setId(pInput.getId());
        dto.setVariantCode(pInput.getVariantCode());
        dto.setVariantName(pInput.getVariantName());
        dto.setStorageQty(pInput.getStorageQty());
        dto.setSoldQty(pInput.getSoldQty());
        dto.setStatus(pInput.getStatus());

        Product lvProduct = pInput.getProduct();
        if (lvProduct != null) {
            dto.setProductId(lvProduct.getId());
            Category lvUnit = lvProduct.getUnit();
            Category lvBrand = lvProduct.getBrand();
            Category lvProductType = lvProduct.getProductType();

            if (!ObjectUtils.isEmpty(lvProductType)) {
                dto.setProductTypeId(lvProductType.getId());
                dto.setProductTypeName(lvProductType.getName());
            }
            if (!ObjectUtils.isEmpty(lvBrand)) {
                dto.setBrandId(lvBrand.getId());
                dto.setBrandName(lvBrand.getName());
            }
            if (!ObjectUtils.isEmpty(lvUnit)) {
                dto.setUnitId(lvUnit.getId());
                dto.setUnitName(lvUnit.getName());
            }
        }

        Category lvColor = pInput.getColor();
        if (lvColor != null) {
            dto.setColorId(lvColor.getId());
            dto.setColorName(lvColor.getName());
        }

        Category lvSize = pInput.getSize();
        if (lvSize != null) {
            dto.setSizeId(lvSize.getId());
            dto.setSizeName(lvSize.getName());
        }

        Category lvFabric = pInput.getFabricType();
        if (!ObjectUtils.isEmpty(lvFabric)) {
            dto.setFabricTypeId(lvFabric.getId());
            dto.setFabricTypeName(lvFabric.getName());
        }

        dto.setUnitCurrency(null);
        dto.setDefectiveQty(pInput.getDefectiveQty());
        dto.setAvailableSalesQty((dto.getStorageQty() != null && dto.getDefectiveQty() != null) ? dto.getStorageQty() - dto.getDefectiveQty(): -1);
        dto.setWeight(pInput.getWeight());
        dto.setNote(pInput.getNote());
        dto.setStatus(pInput.getStatus());
        return dto;
    }
}