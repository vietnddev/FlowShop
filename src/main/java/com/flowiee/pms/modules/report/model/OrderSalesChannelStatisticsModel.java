package com.flowiee.pms.modules.report.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class OrderSalesChannelStatisticsModel {
    private String salesChannelName;
    private String labelColor;
    private Integer numberOfOrders;
    private BigDecimal valueOfOrders;
    private Integer numberOfProducts;
}