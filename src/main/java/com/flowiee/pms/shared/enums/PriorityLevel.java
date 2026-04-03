package com.flowiee.pms.shared.enums;

import lombok.Getter;

@Getter
public enum PriorityLevel {
    L("Low"),
    M("Medium"),
    H("High"),
    U("Urgent");

    private final String description;

    PriorityLevel(String description) {
        this.description = description;
    }
}