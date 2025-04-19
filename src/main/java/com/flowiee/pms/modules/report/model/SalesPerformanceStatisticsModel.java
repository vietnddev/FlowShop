package com.flowiee.pms.modules.report.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class SalesPerformanceStatisticsModel {
    private String employeeName;
    private String employeePosition;
    private BigDecimal totalRevenue;
    private Integer totalTransactions;
    private Float targetAchievementRate;
    private String effectiveSalesTime;
    private Integer numberOfProductsSold;
}