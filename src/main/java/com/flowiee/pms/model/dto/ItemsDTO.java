package com.flowiee.pms.model.dto;

import com.flowiee.pms.base.service.BaseDTO;
import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.entity.sales.OrderCart;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ItemsDTO extends BaseDTO implements Serializable {
    private ProductDetail productDetail;
    private int quantity;
    private String note;
    private OrderCart orderCart;
    private String priceType;
    private BigDecimal price;
    private BigDecimal priceOriginal;
    private BigDecimal extraDiscount;
    private Long productVariantId;
}