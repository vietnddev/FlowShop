package com.flowiee.pms.common.enumeration;

import java.util.Arrays;

public enum ActionStatus {
    DRAFTED("DRAFTED"),
    REOPENED("REOPENED"),
    SUBMITTED("SUBMITTED"),
    CONFIRMED("CONFIRMED"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    CLOSED("CLOSED"),
    INVALID("INVALID"),
    OBSOLETED("OBSOLETED");

    private final String value;

    ActionStatus(String value) {
        this.value = value;
    }

    public static ActionStatus get(String name) {
        return Arrays.stream(ActionStatus.values()).filter(z -> z.name().equals(name))
                .findFirst().orElse(INVALID);
    }

    public String getValue() {
        return this.value;
    }
}