package com.flowiee.pms.common.enumeration;

import lombok.Getter;

@Getter
public enum TicketImportStatus {
    DRAFT("Lưu nháp"),
    COMPLETED("Hoàn thành"),
    CANCEL("Hủy");

    private final String label;

    TicketImportStatus(String label) {
        this.label = label;
    }
}