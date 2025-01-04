package com.flowiee.pms.service.report.impl;

import com.flowiee.pms.entity.report.Report;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.repository.report.ReportRepository;
import com.flowiee.pms.service.report.ReportService;
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