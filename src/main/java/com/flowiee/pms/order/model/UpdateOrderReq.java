package com.flowiee.pms.order.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateOrderReq extends CreateOrderReq {
    private LocalDateTime successfulDeliveryTime;
    private Long cancellationReason;
}