package com.flowiee.pms.modules.inventory.util;

import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.modules.inventory.entity.Product;
import com.flowiee.pms.modules.sales.entity.GarmentFactory;
import com.flowiee.pms.modules.sales.entity.Supplier;
import com.flowiee.pms.modules.inventory.dto.ProductDTO;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductConvert {
    public static Product convertToEntity(ProductDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        Product outputEntity = Product.builder()
            .productCategory(inputDTO.getProductCategory())
            .productName(inputDTO.getProductName())
            //.description(inputDTO.getDescription())
            //.status(inputDTO.getStatus())
            .productType(new Category(inputDTO.getProductType().getId()))
            .brand(new Category(inputDTO.getBrand().getId()))
            .unit(new Category(inputDTO.getUnit().getId()))
            .garmentFactory(new GarmentFactory(inputDTO.getGarmentFactory().getId()))
            .supplier(new Supplier(inputDTO.getSupplier().getId(), null))
            //.productVariantList(inputDTO.getProductVariantList())
            //.listImages(inputDTO.getListImages())
            //.listProductHistories(inputDTO.getListProductHistories())
            .build();

        if (outputEntity.getProductType() == null && inputDTO.getProductTypeId() != null)
            outputEntity.setProductType(new Category(inputDTO.getProductTypeId(), null));

        if (outputEntity.getBrand() == null && inputDTO.getBrandId() != null)
            outputEntity.setBrand(new Category(inputDTO.getBrandId(), null));

        if (outputEntity.getUnit() == null && inputDTO.getUnitId() != null)
            outputEntity.setUnit(new Category(inputDTO.getUnitId(), null));

        if (outputEntity.getGarmentFactory() == null && inputDTO.getGarmentFactoryId() != null)
            outputEntity.setGarmentFactory(new GarmentFactory(inputDTO.getGarmentFactoryId()));

        if (outputEntity.getSupplier() == null && inputDTO.getSupplierId() != null)
            outputEntity.setSupplier(new Supplier(inputDTO.getSupplierId(), inputDTO.getSupplierName()));

        outputEntity.setId(inputDTO.getId());
        outputEntity.setCreatedAt(inputDTO.getCreatedAt());
        outputEntity.setCreatedBy(inputDTO.getCreatedBy());
        outputEntity.setInternalNotes(inputDTO.getInternalNotes());

        return outputEntity;
    }

    public static ProductDTO toDto(Product pInput) {
        ProductDTO dto = new ProductDTO();
        if (pInput != null) {
            dto.setId(pInput.getId());
            dto.setProductCategory(pInput.getProductCategory());
            //dto.setProductType();
            //dto.setBrand();
            //dto.setUnit();
            dto.setProductName(pInput.getProductName());
            dto.setReleaseDate(pInput.getReleaseDate());
            dto.setGender(pInput.getGender());
            dto.setIsSaleOff(pInput.getIsSaleOff());
            dto.setIsHotTrend(pInput.getIsHotTrend());
            dto.setReturnPolicy(pInput.getReturnPolicy());
            dto.setVariantDefault(pInput.getVariantDefault());
            dto.setInternalNotes(pInput.getInternalNotes());
            //dto.setGarmentFactory();
            //dto.setSupplier();
            dto.setStatus("ACT");
            dto.setSoldQty(null);
            dto.setCreatedAt(pInput.getCreatedAt());
            dto.setCreatedBy(pInput.getCreatedBy());

            if (ObjectUtils.isNotEmpty(pInput.getProductType())) {
                //dto.setProductType(inputEntity.getProductType());
                dto.setProductTypeId(pInput.getProductType().getId());
                dto.setProductTypeName(pInput.getProductType().getName());
            }
            if (ObjectUtils.isNotEmpty(pInput.getBrand())) {
                //dto.setBrand(inputEntity.getBrand());
                dto.setBrandId(pInput.getBrand().getId());
                dto.setBrandName(pInput.getBrand().getName());
            }
            if (ObjectUtils.isNotEmpty(pInput.getUnit())) {
                //dto.setUnit(inputEntity.getUnit());
                dto.setUnitId(pInput.getUnit().getId());
                dto.setUnitName(pInput.getUnit().getName());
            }
            if (ObjectUtils.isNotEmpty(pInput.getGarmentFactory())) {
                //dto.setGarmentFactory(inputEntity.getGarmentFactory());
                dto.setGarmentFactoryId(pInput.getGarmentFactory().getId());
                dto.setGarmentFactoryName(pInput.getGarmentFactory().getName());
            }
            if (ObjectUtils.isNotEmpty(pInput.getSupplier())) {
                //dto.setSupplier(inputEntity.getSupplier());
                dto.setSupplierId(pInput.getSupplier().getId());
                dto.setSupplierName(pInput.getSupplier().getName());
            }
        }
        return dto;
    }

    public static List<ProductDTO> convertToDTOs(List<Product> inputEntities) {
        List<ProductDTO> outDTOs = new ArrayList<>();
        if (inputEntities != null) {
            for (Product p : inputEntities) {
                outDTOs.add(toDto(p));
            }
        }
        return outDTOs;
    }
}