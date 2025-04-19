package com.flowiee.pms.modules.system.service;

import com.flowiee.pms.modules.system.model.EximResult;
import com.flowiee.pms.common.enumeration.TemplateExport;

public interface ExportService {
    EximResult exportToExcel(TemplateExport templateExport, Object pCondition, boolean templateOnly);
}