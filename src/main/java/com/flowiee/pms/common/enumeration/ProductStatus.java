package com.flowiee.pms.common.enumeration;

import lombok.Getter;

@Getter
public enum ProductStatus {
    ACT("Available for sales"),
    INA("Inactive"),
    DIS("Discontinued (no longer sold)"),
    OOS("Out of stock");

    //Label can be overrided by Category.name with type is PRODUCT_STATUS
    private String label;

    ProductStatus(String label) {
        this.label = label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}