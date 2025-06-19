package com.flowiee.pms.modules.sales.model;

import lombok.*;

import java.util.List;

@Data
public class CartReq {
    private Long cartId;
    private List<CartItemsReq> items;
}