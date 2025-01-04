package com.flowiee.pms.model.payload;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateOrderReq extends CreateOrderReq {
    private LocalDateTime successfulDeliveryTime;
    private Long cancellationReason;
}