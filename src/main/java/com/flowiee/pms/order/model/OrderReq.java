package com.flowiee.pms.order.model;

import com.flowiee.pms.order.enums.OrderStatus;
import com.flowiee.pms.shared.request.BaseParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Getter
@Setter
public class OrderReq extends BaseParameter {
    private Long orderId;
    private Long paymentMethodId;
    private OrderStatus orderStatus;
    private Long salesChannelId;
    private Long groupCustomerId;
    private Long sellerId;
    private Long customerId;
    private Long branchId;
    private String dateFilter;
    private String txtSearch;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}