package com.flowiee.pms.service.report;

import com.flowiee.pms.entity.report.Report;

public interface ReportService {
    Report findById(String pReportId, boolean pThrowException);
}