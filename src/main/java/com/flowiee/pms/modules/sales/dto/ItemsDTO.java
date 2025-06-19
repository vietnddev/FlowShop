package com.flowiee.pms.modules.sales.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.sales.entity.OrderCart;
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