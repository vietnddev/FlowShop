package com.flowiee.pms.system.enums;

import lombok.Getter;

@Getter
public enum LeaveStatus {
    PENDING("P", "Đang chờ duyệt"),
    APPROVED("A", "Đã duyệt"),
    REJECTED("R", "Từ chối");

    private final String code;
    private String description;

    LeaveStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}