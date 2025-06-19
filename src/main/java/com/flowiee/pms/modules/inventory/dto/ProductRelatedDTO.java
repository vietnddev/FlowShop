package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProductRelatedDTO extends BaseDTO implements Serializable {
    private ProductDTO product;
    private ProductDTO relatedProduct;
}