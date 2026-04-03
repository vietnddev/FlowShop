package com.flowiee.pms.promotion.enums;

import lombok.Getter;

@Getter
public enum PromotionStatus {
    A("Đang áp dụng"),
    I("Đang không áp dụng");
    private final String label;

    PromotionStatus(String label) {
        this.label = label;
    }
}