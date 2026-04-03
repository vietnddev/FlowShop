package com.flowiee.pms.product.dto;

import com.flowiee.pms.shared.base.BaseDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProductDescriptionDTO extends BaseDTO implements Serializable {
    private Long productId;
    private String description;
}