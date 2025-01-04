package com.flowiee.pms.model.payload;

import lombok.*;

import java.util.List;

@Data
public class CartReq {
    private Long cartId;
    private List<CartItemsReq> items;
}