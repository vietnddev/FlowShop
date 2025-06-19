package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.modules.inventory.entity.MaterialHistory;
import com.flowiee.pms.modules.inventory.entity.MaterialTemp;
import com.flowiee.pms.modules.sales.entity.Supplier;
import com.flowiee.pms.modules.media.entity.FileStorage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialDTO extends BaseDTO implements Serializable {
    @Serial
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