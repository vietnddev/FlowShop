package com.flowiee.pms.cart.model;

import lombok.Data;

@Data
public class CartItemsReq {
    private Long productVariantId;
    private Integer quantity;
}