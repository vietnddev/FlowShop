package com.flowiee.pms.modules.sales.model;

import com.flowiee.pms.order.model.CreateOrderReq;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateOrderReq extends CreateOrderReq {
    private LocalDateTime successfulDeliveryTime;
    private Long cancellationReason;
}