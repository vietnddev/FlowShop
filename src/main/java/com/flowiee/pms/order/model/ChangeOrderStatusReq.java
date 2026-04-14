package com.flowiee.pms.order.model;

import com.flowiee.pms.order.dto.OrderReturnItemDTO;
import com.flowiee.pms.order.enums.OrderRefundMethod;
import com.flowiee.pms.order.enums.OrderReturnStatus;
import com.flowiee.pms.order.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ChangeOrderStatusReq {
    // Target status (required)
    private OrderStatus orderStatus;

    //Common field
    private Long orderId;
    private Long processStaffId;
    private String reason;
    private String note;

    // For REFUNDED status
    private LocalDateTime returnDate;
    private List<OrderReturnItemDTO> returnItems;
    private BigDecimal refundAmount;          // Số tiền hoàn
    private OrderRefundMethod refundMethod;   // BANKING, CASH
    private OrderReturnStatus returnStatus;
    private Boolean isRefunded;

    // For COMPLETED status
    private LocalDateTime deliverySuccessTime;

    // For SHIPPING status
    private String trackingCode; // Mã vận đơn
    private String carrier;      // Đơn vị vận chuyển
}