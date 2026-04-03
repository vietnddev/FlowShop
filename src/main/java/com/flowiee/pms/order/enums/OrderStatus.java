package com.flowiee.pms.order.enums;

import lombok.Getter;

import java.util.*;

@Getter
public enum OrderStatus {
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    REFUNDED("Refunded");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public static OrderStatus get(String pOrderStatus) {
        if (pOrderStatus == null || pOrderStatus.isBlank()) {
            return null;
        }
        for (OrderStatus orderStatus : values()) {
            if (orderStatus.name().equalsIgnoreCase(pOrderStatus.trim())) {
                return orderStatus;
            }
        }
        return null;
    }

    public static List<OrderStatus> getAll(OrderStatus exclude) {
        if (exclude == null) {
            return List.of(values());
        }
        List<OrderStatus> statusList = new ArrayList<>();
        for (OrderStatus status : values()) {
            if (!status.equals(exclude)) {
                statusList.add(status);
            }
        }
        return statusList;
    }

    public static LinkedHashMap<String, String> getAllMap(OrderStatus exclude) {
        LinkedHashMap<String, String> statusMap = new LinkedHashMap<>();
        for (OrderStatus status : getAll(exclude)) {
            statusMap.put(status.name(), status.getLabel());
        }
        return statusMap;
    }
}