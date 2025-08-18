package com.flowiee.pms.modules.sales.dto;
import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.modules.sales.utils.OrderRefundMethod;
import com.flowiee.pms.modules.sales.utils.OrderReturnStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderReturnDTO extends BaseDTO {
    private Long orderId;
    private String returnsCode;
    private String reason;
    private String seller;
    private String customerName;
    private LocalDateTime returnDate;
    private OrderRefundMethod refundMethod;
    private BigDecimal refundAmount;
    private Boolean isRefunded;
    private OrderReturnStatus status;
    private List<OrderReturnItemDTO> items;
}