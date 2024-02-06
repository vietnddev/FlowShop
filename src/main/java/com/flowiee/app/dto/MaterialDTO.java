package com.flowiee.app.dto;

import com.flowiee.app.entity.Material;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MaterialDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String code;
    private String name;
    private Integer supplierId;
    private String supplierName;
    private Integer unitId;
    private String unitName;
    private Integer quantity;
    private String location;
    private Boolean status;

    public static MaterialDTO fromMaterial(Material material) {
        MaterialDTO dto = new MaterialDTO();
        dto.setId(material.getId());
        dto.setCode(material.getCode());
        dto.setName(dto.getName());
        if (material.getSupplier() != null) {
            dto.setSupplierId(material.getSupplier().getId());
            dto.setSupplierName(material.getSupplier().getName());
        }
        if (material.getUnit() != null) {
            dto.setUnitId(material.getUnit().getId());
            dto.setUnitName(material.getUnit().getName());
        }
        dto.setQuantity(material.getQuantity());
        dto.setLocation(material.getLocation());
        dto.setStatus(material.isStatus());
        return dto;
    }

    public static List<MaterialDTO> fromMaterials(List<Material> materials) {
        List<MaterialDTO> list = new ArrayList<>();
        for (Material m : materials) {
            list.add(MaterialDTO.fromMaterial(m));
        }
        return list;
    }
}