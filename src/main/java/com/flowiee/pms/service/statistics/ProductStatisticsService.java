package com.flowiee.pms.service.statistics;

import com.flowiee.pms.model.statistics.DefectiveProductStatisticsModel;

import java.util.List;

public interface ProductStatisticsService {
    List<DefectiveProductStatisticsModel> getDefectiveProduct();
}