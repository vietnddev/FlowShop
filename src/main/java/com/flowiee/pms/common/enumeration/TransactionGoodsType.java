package com.flowiee.pms.common.enumeration;

import java.util.Arrays;

public enum TransactionGoodsType {
    RECEIPT("RECEIPT"),
    ISSUE("ISSUE"),
    INVALID("INVALID");

    private final String value;

    TransactionGoodsType(String value) {
        this.value = value;
    }

    public static TransactionGoodsType get(String name) {
        return Arrays.stream(TransactionGoodsType.values()).filter(z -> z.name().equals(name))
                .findFirst().orElse(INVALID);
    }

    public String getValue() {
        return this.value;
    }
}