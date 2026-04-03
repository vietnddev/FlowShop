package com.flowiee.pms.cart.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CartItemModel {
    private Long cartId;
    private Long itemId;
    private String itemName;
    private Long productComboId;
    private Long productVariantId;
}