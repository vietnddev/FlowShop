package com.flowiee.pms.model.payload;

import lombok.Data;

@Data
public class CartItemsReq {
    private Long productVariantId;
    private Integer quantity;
}