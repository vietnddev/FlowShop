package com.flowiee.pms.model.dto;

import com.flowiee.pms.base.service.BaseDTO;
import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.entity.product.MaterialHistory;
import com.flowiee.pms.entity.product.MaterialTemp;
import com.flowiee.pms.entity.sales.Supplier;
import com.flowiee.pms.entity.system.FileStorage;
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