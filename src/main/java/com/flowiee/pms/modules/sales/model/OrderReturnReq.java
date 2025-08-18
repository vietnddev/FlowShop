package com.flowiee.pms.modules.sales.model;

import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.modules.sales.dto.OrderReturnItemDTO;
import com.flowiee.pms.modules.sales.utils.OrderRefundMethod;
import com.flowiee.pms.modules.sales.utils.OrderReturnStatus;
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