package com.flowiee.pms.report.service.impl;

import com.flowiee.pms.report.entity.Report;
import com.flowiee.pms.shared.exception.EntityNotFoundException;
import com.flowiee.pms.report.reporitory.ReportRepository;
import com.flowiee.pms.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;

    @Override
    public Report findById(String pReportId, boolean pThrowException) {
        Optional<Report> reportOptional = reportRepository.findById(pReportId);
        if (reportOptional.isPresent()) {
            return reportOptional.get();
        } else {
            if (pThrowException) {
                throw new EntityNotFoundException(new Object[] {"report"}, null, null);
            }
            return null;
        }
    }
}