package com.flowiee.pms.report.service;

import com.flowiee.pms.report.entity.Report;

public interface ReportService {
    Report findById(String pReportId, boolean pThrowException);
}