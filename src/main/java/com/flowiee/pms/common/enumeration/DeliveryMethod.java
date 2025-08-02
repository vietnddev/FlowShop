package com.flowiee.pms.common.enumeration;

import lombok.Getter;

@Getter
public enum DeliveryMethod {
    COD("Cash on Delivery"),
    ISP("In-Store Pickup"),
    STD("Standard Shipping");

    private String name;

    DeliveryMethod(String name) {
        this.name = name;
    }
}