package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.modules.inventory.entity.Product;
import com.flowiee.pms.modules.inventory.entity.ProductAttribute;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
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