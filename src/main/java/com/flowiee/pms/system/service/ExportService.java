package com.flowiee.pms.system.service;

import com.flowiee.pms.system.model.EximResult;
import com.flowiee.pms.shared.enums.TemplateExport;

public interface ExportService {
    EximResult exportToExcel(TemplateExport templateExport, Object pCondition, boolean templateOnly);
}