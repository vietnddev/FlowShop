package com.flowiee.pms.service;

import com.flowiee.pms.model.EximResult;
import com.flowiee.pms.common.enumeration.TemplateExport;

public interface ExportService {
    EximResult exportToExcel(TemplateExport templateExport, Object pCondition, boolean templateOnly);
}