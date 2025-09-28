package com.flowiee.pms.modules.inventory.enums;

import com.flowiee.pms.common.utils.CoreUtils;

public enum TransactionGoodsType {
    IMPORT, EXPORT;

    public static TransactionGoodsType fromStr(String pTranType) {
        for (TransactionGoodsType lvTranType : values()) {
            if (lvTranType.name().equals(CoreUtils.trim(pTranType).toUpperCase())) {
                return lvTranType;
            }
        }

        throw new IllegalArgumentException();
    }
}