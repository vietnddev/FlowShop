package com.flowiee.pms.modules.sales.model;

import com.flowiee.pms.common.enumeration.OrderStatus;
import com.flowiee.pms.common.model.BaseParameter;
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