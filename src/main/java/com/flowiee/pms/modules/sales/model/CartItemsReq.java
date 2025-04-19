package com.flowiee.pms.modules.sales.model;

import lombok.Data;

@Data
public class CartItemsReq {
    private Long productVariantId;
    private Integer quantity;
}