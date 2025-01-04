package com.flowiee.pms.common.enumeration;

import lombok.Getter;

@Getter
public enum StorageStatus {
    A("Đang được sử dụng"),
    I("Không sử dụng");
    private final String label;

    StorageStatus(String label) {
        this.label = label;
    }
}