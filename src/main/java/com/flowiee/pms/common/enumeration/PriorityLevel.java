package com.flowiee.pms.common.enumeration;

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