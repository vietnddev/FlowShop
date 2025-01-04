package com.flowiee.pms.common.enumeration;

import lombok.Getter;

@Getter
public enum TicketImportAction {
    INIT("Khởi tạo variant - IM"),
    CANCELED_DELIVERY("Nhận hàng hủy giao"),
    IMPORT_INTO_STORAGE("Nhập hàng vào kho");

    private final String label;

    TicketImportAction(String label) {
        this.label = label;
    }
}