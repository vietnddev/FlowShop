package com.flowiee.pms.inventory.dto;

import com.flowiee.pms.shared.base.BaseDTO;
import com.flowiee.pms.system.entity.Category;
import com.flowiee.pms.inventory.entity.MaterialHistory;
import com.flowiee.pms.inventory.entity.MaterialTemp;
import com.flowiee.pms.supplier.entity.Supplier;
import com.flowiee.pms.media.entity.FileStorage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialDTO extends BaseDTO implements Serializable {
    static final long serialVersionUID = 1L;

    Long supplierId;
    String supplierName;
    Long unitId;
    String unitName;

    Category brand;
    Supplier supplier;
    String code;
    String name;
    Integer quantity;
    Category unit;
    String location;
    String note;
    boolean status;
    List<MaterialHistory> listMaterialHistory;
    List<FileStorage> listImages;
    List<MaterialTemp> listMaterialTemp;
}