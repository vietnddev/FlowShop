package com.flowiee.pms.model.dto;

import com.flowiee.pms.base.service.BaseDTO;
import com.flowiee.pms.entity.product.Product;
import com.flowiee.pms.entity.product.ProductAttribute;
import com.flowiee.pms.entity.product.ProductDetail;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class ProductHistoryDTO extends BaseDTO implements Serializable {
    private Product product;
    private ProductDetail productDetail;
    private ProductAttribute productAttribute;
    private String title;
    private String field;
    private String oldValue;
    private String newValue;
    private Long productId;
    private Long productVariantId;
    private Long productAttributeId;
}