package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProductAttributeDTO extends BaseDTO implements Serializable {
    private Long productId;
    private String attributeName;
    private String attributeValue;
    private Integer sort;
    private Boolean status;
}