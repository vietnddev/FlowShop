package com.flowiee.pms.common.enumeration;

import lombok.Getter;

@Getter
public enum LedgerTranStatus {
    COMPLETED("Hoàn thành"),
    CANCEL("Đã hủy");
    private final String description;

    LedgerTranStatus(final String description) {
        this.description = description;
    }
}