package com.flowiee.pms.modules.report.service;

import com.flowiee.pms.modules.report.model.DefectiveProductStatisticsModel;

import java.util.List;

public interface ProductStatisticsService {
    List<DefectiveProductStatisticsModel> getDefectiveProduct();
}