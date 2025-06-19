package com.flowiee.pms.modules.report.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DefectiveProductStatisticsModel {
    private String productName;
    private Integer defectiveQuantity;
    private Integer totalQuantity;
    private String rate;
}