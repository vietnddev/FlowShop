package com.flowiee.pms.common.converter;

import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.entity.product.Product;
import com.flowiee.pms.entity.sales.GarmentFactory;
import com.flowiee.pms.entity.sales.Supplier;
import com.flowiee.pms.model.dto.ProductDTO;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductConvert {
    public static Product convertToEntity(ProductDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        Product outputEntity = Product.builder()
            .PID(inputDTO.getPID())
            .productName(inputDTO.getProductName())
            //.description(inputDTO.getDescription())
            //.status(inputDTO.getStatus())
            .productType(inputDTO.getProductType())
            .brand(inputDTO.getBrand())
            .unit(inputDTO.getUnit())
            .garmentFactory(inputDTO.getGarmentFactory())
            .supplier(inputDTO.getSupplier())
            .productVariantList(inputDTO.getProductVariantList())
            .listImages(inputDTO.getListImages())
            .listProductHistories(inputDTO.getListProductHistories())
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

    public static ProductDTO convertToDTO(Product inputEntity) {
        ProductDTO dto = new ProductDTO();
        if (inputEntity != null) {
            dto.setId(inputEntity.getId());
            dto.setProductName(inputEntity.getProductName());
            if (ObjectUtils.isNotEmpty(inputEntity.getProductType())) {
                dto.setProductType(inputEntity.getProductType());
                dto.setProductTypeId(inputEntity.getProductType().getId());
                dto.setProductTypeName(inputEntity.getProductType().getName());
            }
            if (ObjectUtils.isNotEmpty(inputEntity.getBrand())) {
                dto.setBrand(inputEntity.getBrand());
                dto.setBrandId(inputEntity.getBrand().getId());
                dto.setBrandName(inputEntity.getBrand().getName());
            }
            if (ObjectUtils.isNotEmpty(inputEntity.getUnit())) {
                dto.setUnit(inputEntity.getUnit());
                dto.setUnitId(inputEntity.getUnit().getId());
                dto.setUnitName(inputEntity.getUnit().getName());
            }
            if (ObjectUtils.isNotEmpty(inputEntity.getGarmentFactory())) {
                dto.setGarmentFactory(inputEntity.getGarmentFactory());
                dto.setGarmentFactoryId(inputEntity.getGarmentFactory().getId());
                dto.setGarmentFactoryName(inputEntity.getGarmentFactory().getName());
            }
            if (ObjectUtils.isNotEmpty(inputEntity.getSupplier())) {
                dto.setSupplier(inputEntity.getSupplier());
                dto.setSupplierId(inputEntity.getSupplier().getId());
                dto.setSupplierName(inputEntity.getSupplier().getName());
            }
            dto.setStatus("ACT");
            dto.setSoldQty(null);
            dto.setCreatedAt(inputEntity.getCreatedAt());
            dto.setCreatedBy(inputEntity.getCreatedBy());

            dto.setInternalNotes(inputEntity.getInternalNotes());
        }
        return dto;
    }

    public static List<ProductDTO> convertToDTOs(List<Product> inputEntities) {
        List<ProductDTO> outDTOs = new ArrayList<>();
        if (inputEntities != null) {
            for (Product p : inputEntities) {
                outDTOs.add(convertToDTO(p));
            }
        }
        return outDTOs;
    }
}