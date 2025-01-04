package com.flowiee.pms.common.enumeration;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TransactionGoodsStatus {
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    OBSOLETED("OBSOLETED"),
    CLOSED("CLOSED"),
    INVALID("INVALID"),
    SUBMITTED("SUBMITTED");

    private final String value;

    TransactionGoodsStatus(String value) {
        this.value = value;
    }

    public static TransactionGoodsStatus get(String name) {
        return Arrays.stream(TransactionGoodsStatus.values()).filter(z -> z.name().equals(name))
                .findFirst().orElse(INVALID);
    }
}