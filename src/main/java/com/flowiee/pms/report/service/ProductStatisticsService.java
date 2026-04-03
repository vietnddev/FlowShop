package com.flowiee.pms.report.service;

import com.flowiee.pms.report.model.DefectiveProductStatisticsModel;

import java.util.List;

public interface ProductStatisticsService {
    List<DefectiveProductStatisticsModel> getDefectiveProduct();
}