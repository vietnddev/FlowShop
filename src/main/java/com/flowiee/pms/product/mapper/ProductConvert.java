package com.flowiee.pms.product.mapper;

import com.flowiee.pms.modules.sales.dto.GarmentFactoryDTO;
import com.flowiee.pms.modules.sales.dto.SupplierDTO;
import com.flowiee.pms.modules.system.dto.CategoryDTO;
import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.product.entity.Product;
import com.flowiee.pms.modules.sales.entity.GarmentFactory;
import com.flowiee.pms.modules.sales.entity.Supplier;
import com.flowiee.pms.product.dto.ProductDTO;
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

        if (outputEntity.getProductType() == null && inputDTO.getProductType() != null)
            outputEntity.setProductType(new Category(inputDTO.getProductType().getId(), inputDTO.getProductType().getName()));

        if (outputEntity.getBrand() == null && inputDTO.getBrand() != null)
            outputEntity.setBrand(new Category(inputDTO.getBrand().getId(), inputDTO.getBrand().getName()));

        if (outputEntity.getUnit() == null && inputDTO.getUnit() != null)
            outputEntity.setUnit(new Category(inputDTO.getUnit().getId(), inputDTO.getUnit().getName()));

        if (outputEntity.getGarmentFactory() == null && inputDTO.getGarmentFactory() != null)
            outputEntity.setGarmentFactory(new GarmentFactory(inputDTO.getGarmentFactory().getId()));

        if (outputEntity.getSupplier() == null && inputDTO.getSupplier() != null)
            outputEntity.setSupplier(new Supplier(inputDTO.getSupplier().getId(), inputDTO.getSupplier().getName()));

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
            //dto.setStatus(null);
            //dto.setSoldQty(null);
            dto.setCreatedAt(pInput.getCreatedAt());
            dto.setCreatedBy(pInput.getCreatedBy());

            if (ObjectUtils.isNotEmpty(pInput.getProductType())) {
                dto.setProductType(new CategoryDTO(pInput.getProductType().getId(), pInput.getProductType().getName()));
//                dto.setProductTypeId(pInput.getProductType().getId());
//                dto.setProductTypeName(pInput.getProductType().getName());
            }
            if (ObjectUtils.isNotEmpty(pInput.getBrand())) {
                dto.setBrand(new CategoryDTO(pInput.getBrand().getId(), pInput.getBrand().getName()));
//                dto.setBrandId(pInput.getBrand().getId());
//                dto.setBrandName(pInput.getBrand().getName());
            }
            if (ObjectUtils.isNotEmpty(pInput.getUnit())) {
                dto.setUnit(new CategoryDTO(pInput.getUnit().getId(), pInput.getUnit().getName()));
//                dto.setUnitId(pInput.getUnit().getId());
//                dto.setUnitName(pInput.getUnit().getName());
            }
            if (ObjectUtils.isNotEmpty(pInput.getGarmentFactory())) {
                dto.setGarmentFactory(new GarmentFactoryDTO(pInput.getGarmentFactory().getId(), pInput.getGarmentFactory().getName()));
//                dto.setGarmentFactoryId(pInput.getGarmentFactory().getId());
//                dto.setGarmentFactoryName(pInput.getGarmentFactory().getName());
            }
            if (ObjectUtils.isNotEmpty(pInput.getSupplier())) {
                dto.setSupplier(new SupplierDTO(pInput.getSupplier().getId(), pInput.getSupplier().getName()));
//                dto.setSupplierId(pInput.getSupplier().getId());
//                dto.setSupplierName(pInput.getSupplier().getName());
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