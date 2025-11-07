package com.flowiee.pms.common.enumeration;

import lombok.Getter;

@Getter
public enum ProductStatus {
    ACT("Available for sale"),
    INA("Inactive"),
    DIS("Discontinued (no longer sold)"),
    OOS("Out of stock");

    private String label;

    ProductStatus(String label) {
        this.label = label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}