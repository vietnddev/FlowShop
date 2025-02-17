package com.flowiee.pms.common.converter;

import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.entity.product.Product;
import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.entity.sales.GarmentFactory;
import com.flowiee.pms.entity.sales.Supplier;
import com.flowiee.pms.model.dto.ProductVariantDTO;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductVariantConvert {
    public static List<ProductVariantDTO> entitiesToDTOs(List<ProductDetail> inputEntities) {
        List<ProductVariantDTO> outputDTOs = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(inputEntities)) {
            for (ProductDetail p : inputEntities) {
                outputDTOs.add(entityToDTO(p));
            }
        }
        return outputDTOs;
    }

    public static List<ProductDetail> dTOsToEntities(List<ProductVariantDTO> inputDTOs) {
        List<ProductDetail> outputEntities = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(inputDTOs)) {
            for (ProductVariantDTO p : inputDTOs) {
                outputEntities.add(dtoToEntity(p));
            }
        }
        return outputEntities;
    }

    public static ProductVariantDTO entityToDTO(ProductDetail inputEntity) {
        if (ObjectUtils.isEmpty(inputEntity)) {
            return null;
        }
        ProductVariantDTO outputDTO = new ProductVariantDTO();
        outputDTO.setId(inputEntity.getId());
        outputDTO.setProductId(inputEntity.getProduct().getId());
        outputDTO.setVariantCode(inputEntity.getVariantCode());
        outputDTO.setVariantName(inputEntity.getVariantName());
        outputDTO.setStorageQty(inputEntity.getStorageQty());
        outputDTO.setSoldQty(inputEntity.getSoldQty());
        outputDTO.setStatus(inputEntity.getStatus());
        if (ObjectUtils.isNotEmpty(inputEntity.getProduct().getProductType())) {
            outputDTO.setProductTypeId(inputEntity.getProduct().getProductType().getId());
            outputDTO.setProductTypeName(inputEntity.getProduct().getProductType().getName());
        }
        if (ObjectUtils.isNotEmpty(inputEntity.getProduct().getBrand())) {
            outputDTO.setBrandId(inputEntity.getProduct().getBrand().getId());
            outputDTO.setBrandName(inputEntity.getProduct().getBrand().getName());
        }
        if (ObjectUtils.isNotEmpty(inputEntity.getProduct().getUnit())) {
            outputDTO.setUnitId(inputEntity.getProduct().getUnit().getId());
            outputDTO.setUnitName(inputEntity.getProduct().getUnit().getName());
        }
        outputDTO.setColorId(inputEntity.getColor().getId());
        outputDTO.setColorName(inputEntity.getColor().getName());
        outputDTO.setSizeId(inputEntity.getSize().getId());
        outputDTO.setSizeName(inputEntity.getSize().getName());
        if (inputEntity.getFabricType() != null) {
            outputDTO.setFabricType(inputEntity.getFabricType());
            outputDTO.setFabricTypeId(inputEntity.getFabricType().getId());
            outputDTO.setFabricTypeName(inputEntity.getFabricType().getName());
        }
        if (ObjectUtils.isNotEmpty(inputEntity.getGarmentFactory())) {
            outputDTO.setGarmentFactoryId(inputEntity.getGarmentFactory().getId());
            outputDTO.setGarmentFactoryName(inputEntity.getGarmentFactory().getName());
        }
        if (ObjectUtils.isNotEmpty(inputEntity.getSupplier())) {
            outputDTO.setSupplierId(inputEntity.getSupplier().getId());
            outputDTO.setSupplierName(inputEntity.getSupplier().getName());
        }
        outputDTO.setUnitCurrency(null);
//        if (AppConstants.PRODUCT_STATUS.A.name().equals(inputEntity.getStatus())) {
//            outputDTO.setStatus(AppConstants.PRODUCT_STATUS.A.getLabel());
//        } else if (AppConstants.PRODUCT_STATUS.I.name().equals(inputEntity.getStatus())) {
//            outputDTO.setStatus(AppConstants.PRODUCT_STATUS.I.getLabel());
//        }
        outputDTO.setDefectiveQty(inputEntity.getDefectiveQty());
        outputDTO.setAvailableSalesQty(outputDTO.getStorageQty() - outputDTO.getDefectiveQty());
        outputDTO.setWeight(inputEntity.getWeight());
        outputDTO.setNote(inputEntity.getNote());
        outputDTO.setStatus(inputEntity.getStatus());

        //outputDTO.setListImages(inputEntity.getListImages());
        outputDTO.setListProductVariantPrice(inputEntity.getListProductVariantPrice());

        return outputDTO;
    }

    public static ProductDetail dtoToEntity(ProductVariantDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        ProductDetail outputEntity = ProductDetail.builder()
            .product(inputDTO.getProduct())
            .color(inputDTO.getColor())
            .size(inputDTO.getSize())
            .fabricType(inputDTO.getFabricType())
            .garmentFactory(inputDTO.getGarmentFactory())
            .supplier(inputDTO.getSupplier())
            .variantCode(inputDTO.getVariantCode())
            .variantName(inputDTO.getVariantName())
            .storageQty(inputDTO.getStorageQty())
            .soldQty(inputDTO.getSoldQty())
            .defectiveQty(inputDTO.getDefectiveQty())
            .weight(inputDTO.getWeight())
            .note(inputDTO.getNote())
            .status(inputDTO.getStatus())
            .build();
        outputEntity.setId(inputDTO.getId());

        if (outputEntity.getProduct() == null && inputDTO.getProductId() != null)
            outputEntity.setProduct(new Product(inputDTO.getProductId()));

        if (outputEntity.getColor() == null && inputDTO.getColorId() != null)
            outputEntity.setColor(new Category(inputDTO.getColorId(), inputDTO.getColorName()));

        if (outputEntity.getSize() == null && inputDTO.getSizeId() != null)
            outputEntity.setSize(new Category(inputDTO.getSizeId(), inputDTO.getSizeName()));

        if (outputEntity.getFabricType() == null && inputDTO.getFabricTypeId() != null)
            outputEntity.setFabricType(new Category(inputDTO.getFabricTypeId(), inputDTO.getFabricTypeName()));

        if (outputEntity.getGarmentFactory() == null && inputDTO.getGarmentFactoryId() != null)
            outputEntity.setGarmentFactory(new GarmentFactory(inputDTO.getGarmentFactoryId()));

        if (outputEntity.getSupplier() == null && inputDTO.getSupplierId() != null)
            outputEntity.setSupplier(new Supplier(inputDTO.getSupplierId(), inputDTO.getSupplierName()));

        return outputEntity;
    }
}