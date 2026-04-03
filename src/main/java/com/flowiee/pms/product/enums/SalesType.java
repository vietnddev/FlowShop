package com.flowiee.pms.product.enums;

import lombok.Getter;

@Getter
public enum SalesType {
    L("Giá bán lẻ"),
    S("Giá sỉ");
    private final String label;

    SalesType(String label) {
        this.label = label;
    }
}