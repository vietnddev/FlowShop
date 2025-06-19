package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class MaterialHistoryDTO extends BaseDTO implements Serializable {
    private MaterialDTO material;
    private String title;
    private String fieldName;
    private String oldValue;
    private String newValue;
}