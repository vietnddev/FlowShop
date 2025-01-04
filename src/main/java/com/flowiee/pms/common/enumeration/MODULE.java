package com.flowiee.pms.common.enumeration;

import lombok.Getter;

@Getter
public enum MODULE {
    DASHBOARD("Dashboard"),
    PRODUCT("Quản lý sản phẩm"),
    SALES("Quản lý bán hàng"),
    SYSTEM("Quản trị hệ thống"),
    CATEGORY("Danh mục hệ thống"),
    STORAGE("Kho");

    private final String label;

    MODULE(String label) {
        this.label = label;
    }
}