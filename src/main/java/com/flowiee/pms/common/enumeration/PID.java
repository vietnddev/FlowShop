package com.flowiee.pms.common.enumeration;

import lombok.Getter;

@Getter
public enum PID {
    CLOTHES("CL", "Thời trang"),
    FRUIT("FR", "Hoa quả"),
    SOUVENIR("SO", "Đồ lưu niệm");

    private final String id;
    private final String label;

    PID(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public static PID get(String pPID) {
        if (pPID == null || pPID.isBlank()) {
            return null;
        }
        for (PID vPID : values()) {
            if (vPID.id.equalsIgnoreCase(pPID.trim())) {
                return vPID;
            }
        }
        return null;
    }
}