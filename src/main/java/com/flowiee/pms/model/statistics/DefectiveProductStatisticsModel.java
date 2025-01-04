package com.flowiee.pms.model.statistics;

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