package com.flowiee.pms.modules.sales.model;

import com.flowiee.pms.common.enumeration.OrderStatus;
import com.flowiee.pms.common.model.BaseParameter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
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