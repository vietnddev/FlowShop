package com.flowiee.pms.model.dto;

import com.flowiee.pms.base.service.BaseDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProductRelatedDTO extends BaseDTO implements Serializable {
    private ProductDTO product;
    private ProductDTO relatedProduct;
}