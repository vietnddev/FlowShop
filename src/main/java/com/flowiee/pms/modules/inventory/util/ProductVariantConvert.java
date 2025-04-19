package com.flowiee.pms.modules.inventory.util;

import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductVariantConvert {
    public static List<ProductVariantDTO> entitiesToDTOs(List<ProductDetail> inputEntities) {
        List<ProductVariantDTO> outputDTOs = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(inputEntities)) {
            for (ProductDetail p : inputEntities) {
                outputDTOs.add(toDto(p));
            }
        }
        return outputDTOs;
    }

    public static ProductVariantDTO toDto(ProductDetail pInput) {
        ProductVariantDTO dto = new ProductVariantDTO();
        if (pInput != null) {
            dto.setId(pInput.getId());
            //dto.setProduct();
            dto.setVariantCode(pInput.getVariantCode());
            dto.setVariantName(pInput.getVariantName());
            //dto.setColor();
            //dto.setSize();
            //dto.setFabricType();
//            dto.setStorageQty();
//            dto.setSoldQty();
//            dto.setDefectiveQty();
//            dto.setWeight();
//            dto.setDimensions();
//            dto.setSku();
//            dto.setSupplierSku();
//            dto.setWarrantyPeriod();
//            dto.setSoleMaterial();
//            dto.setHeelHeight();
//            dto.setDiscontinuedDate();
//            dto.setIsLimitedEdition();
//            dto.setPattern();
//            dto.setLowStockThreshold();
//            dto.setOutOfStockDate();
//            dto.setManufacturingCountry();
//            dto.setManufacturingDate();
//            dto.setExpiryDate();
//            dto.setStorageInstructions();
//            dto.setUvProtection();
//            dto.setIsMachineWashable();
//            dto.setNote();//will be remove
//            dto.setStatus();
//            dto.setAvailableSalesQty();

            dto.setProductId(pInput.getProduct().getId());
            dto.setStorageQty(pInput.getStorageQty());
            dto.setSoldQty(pInput.getSoldQty());
            dto.setStatus(pInput.getStatus());
            if (ObjectUtils.isNotEmpty(pInput.getProduct().getProductType())) {
                dto.setProductTypeId(pInput.getProduct().getProductType().getId());
                dto.setProductTypeName(pInput.getProduct().getProductType().getName());
            }
            if (ObjectUtils.isNotEmpty(pInput.getProduct().getBrand())) {
                dto.setBrandId(pInput.getProduct().getBrand().getId());
                dto.setBrandName(pInput.getProduct().getBrand().getName());
            }
            if (ObjectUtils.isNotEmpty(pInput.getProduct().getUnit())) {
                dto.setUnitId(pInput.getProduct().getUnit().getId());
                dto.setUnitName(pInput.getProduct().getUnit().getName());
            }
            dto.setColorId(pInput.getColor().getId());
            dto.setColorName(pInput.getColor().getName());
            dto.setSizeId(pInput.getSize().getId());
            dto.setSizeName(pInput.getSize().getName());
            if (pInput.getFabricType() != null) {
                //outputDTO.setFabricType(inputEntity.getFabricType());
                dto.setFabricTypeId(pInput.getFabricType().getId());
                dto.setFabricTypeName(pInput.getFabricType().getName());
            }
            dto.setUnitCurrency(null);
//        if (AppConstants.PRODUCT_STATUS.A.name().equals(inputEntity.getStatus())) {
//            outputDTO.setStatus(AppConstants.PRODUCT_STATUS.A.getLabel());
//        } else if (AppConstants.PRODUCT_STATUS.I.name().equals(inputEntity.getStatus())) {
//            outputDTO.setStatus(AppConstants.PRODUCT_STATUS.I.getLabel());
//        }
            dto.setDefectiveQty(pInput.getDefectiveQty());
            dto.setAvailableSalesQty(dto.getStorageQty() - dto.getDefectiveQty());
            dto.setWeight(pInput.getWeight());
            dto.setNote(pInput.getNote());
            dto.setStatus(pInput.getStatus());

            //outputDTO.setListImages(inputEntity.getListImages());
            //outputDTO.setPriceList(inputEntity.getPriceList());
        }
        return dto;
    }
}