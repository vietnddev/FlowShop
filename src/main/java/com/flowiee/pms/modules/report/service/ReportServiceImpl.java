package com.flowiee.pms.modules.report.service;

import com.flowiee.pms.modules.report.entity.Report;
import com.flowiee.pms.common.exception.EntityNotFoundException;
import com.flowiee.pms.modules.report.repository.ReportRepository;
import com.flowiee.pms.modules.report.service.ReportService;
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