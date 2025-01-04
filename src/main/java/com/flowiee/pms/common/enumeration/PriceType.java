package com.flowiee.pms.common.enumeration;

import lombok.Getter;

@Getter
public enum PriceType {
    L("Giá bán lẻ"),
    S("Giá sỉ");
    private final String label;

    PriceType(String label) {
        this.label = label;
    }
}