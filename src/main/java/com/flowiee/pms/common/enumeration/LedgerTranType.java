package com.flowiee.pms.common.enumeration;

import lombok.Getter;

@Getter
public enum LedgerTranType {
    PT("Phiếu thu"),
    PC("Phiếu chi");
    private final String description;

    LedgerTranType(final String description) {
        this.description = description;
    }
}