package com.flowiee.pms.order.model;

import com.flowiee.pms.shared.base.BaseDTO;
import com.flowiee.pms.order.dto.OrderReturnItemDTO;
import com.flowiee.pms.order.enums.OrderRefundMethod;
import com.flowiee.pms.order.enums.OrderReturnStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderReturnReq extends BaseDTO implements Serializable {
    private Long orderId;
    private String reason;
    private LocalDateTime returnDate;
    private OrderRefundMethod refundMethod;
    private BigDecimal refundAmount;
    private Boolean isRefunded;
    private OrderReturnStatus status;
    private Long staffId;
    private List<OrderReturnItemDTO> items;
}