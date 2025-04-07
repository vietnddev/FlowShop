package com.flowiee.pms.model.dto;

import com.flowiee.pms.base.service.BaseDTO;
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