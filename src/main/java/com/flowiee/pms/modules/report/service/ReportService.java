package com.flowiee.pms.modules.report.service;

import com.flowiee.pms.modules.report.entity.Report;

public interface ReportService {
    Report findById(String pReportId, boolean pThrowException);
}