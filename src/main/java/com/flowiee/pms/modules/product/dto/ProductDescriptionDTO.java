package com.flowiee.pms.modules.product.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProductDescriptionDTO extends BaseDTO implements Serializable {
    private Long productId;
    private String description;
}