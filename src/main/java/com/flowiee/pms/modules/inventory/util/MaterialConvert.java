package com.flowiee.pms.modules.inventory.util;

import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.modules.inventory.entity.Material;
import com.flowiee.pms.modules.sales.entity.Supplier;
import com.flowiee.pms.modules.inventory.dto.MaterialDTO;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class MaterialConvert {
    public static Material convertToEntity(MaterialDTO inputDTO) {
        Material outEntity = Material.builder()
            .code(inputDTO.getCode())
            .name(inputDTO.getName())
            .supplier(inputDTO.getSupplier())
            .unit(inputDTO.getUnit())
            .quantity(inputDTO.getQuantity())
            .location(inputDTO.getLocation())
            .note(inputDTO.getNote())
            .status(inputDTO.isStatus())
            .build();
        outEntity.setId(inputDTO.getId());

        if (outEntity.getSupplier() == null && inputDTO.getSupplierId() != null)
            outEntity.setSupplier(new Supplier(inputDTO.getSupplierId(), inputDTO.getSupplierName()));

        if (outEntity.getUnit() == null && inputDTO.getUnitId() != null)
            outEntity.setUnit(new Category(inputDTO.getUnitId(), inputDTO.getUnitName()));

        return outEntity;
    }

    public static MaterialDTO convertToDTO(Material inputEntity) {
        MaterialDTO outDTO = new MaterialDTO();
        outDTO.setId(inputEntity.getId());
        outDTO.setCode(inputEntity.getCode());
        outDTO.setName(outDTO.getName());
        if (inputEntity.getSupplier() != null) {
            outDTO.setSupplierId(inputEntity.getSupplier().getId());
            outDTO.setSupplierName(inputEntity.getSupplier().getName());
        }
        if (inputEntity.getUnit() != null) {
            outDTO.setUnitId(inputEntity.getUnit().getId());
            outDTO.setUnitName(inputEntity.getUnit().getName());
        }
        outDTO.setQuantity(inputEntity.getQuantity());
        outDTO.setLocation(inputEntity.getLocation());
        outDTO.setNote(inputEntity.getNote());
        outDTO.setStatus(inputEntity.isStatus());
        return outDTO;
    }

    public static List<MaterialDTO> convertToDTOs(List<Material> inputEntities) {
        List<MaterialDTO> outputDTOs = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(inputEntities)) {
            for (Material m : inputEntities) {
                outputDTOs.add(convertToDTO(m));
            }
        }
        return outputDTOs;
    }

    public static List<Material> convertToEntities(List<MaterialDTO> inputDTOs) {
        List<Material> outputEntities = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(inputDTOs)) {
            for (MaterialDTO m : inputDTOs) {
                outputEntities.add(convertToEntity(m));
            }
        }
        return outputEntities;
    }
}