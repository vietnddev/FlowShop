package com.flowiee.pms.common.enumeration;

import lombok.Getter;

import java.util.*;

@Getter
public enum OrderStatus {
    PEND("Pending", ""),                  // Đang chờ xử lý
    CONF("Confirmed", ""),                // Đã xác nhận
    PROC("Processing", ""),               // Đang xử lý
    SHIP("Shipped", ""),                  // Đã giao cho đơn vị vận chuyển
    DLVD("Delivered", ""),                // Giao thành công
    CNCL("Cancelled", ""),                // Đã hủy
    FRTND("Returned all", ""),            // Đã trả toàn bộ hàng
    RTND("Returned a part", ""),          // Đã trả một phần hàng
    //FAIL("Failed", ""),                   // Lỗi
    //HOLD("On Hold", ""),                  // Tạm dừng
    //RFND("Refunded", ""),                 // Hoàn tiền
    PART_DLVD("Partially Delivered", ""), // Giao một phần
    //EXPD("Expired", ""),                  // Hết hạn
    AWTP("Awaiting Pickup", ""),          // Chờ lấy hàng
    DSPT("Disputed", ""),                 // Đang tranh chấp
    ALL("All", "");                       // Tất cả trạng thái

    private String name;
    private String description;

    OrderStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static OrderStatus get(String pOrderStatus) {
        if (pOrderStatus == null || pOrderStatus.isBlank()) {
            return null;
        }
        for (OrderStatus orderStatus : values()) {
            if (orderStatus.name.equalsIgnoreCase(pOrderStatus.trim())) {
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
            statusMap.put(status.name(), status.getName());
        }
        return statusMap;
    }
}