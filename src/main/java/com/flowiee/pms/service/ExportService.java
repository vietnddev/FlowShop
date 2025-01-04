package com.flowiee.pms.service;

import com.flowiee.pms.model.EximModel;
import com.flowiee.pms.common.enumeration.TemplateExport;

public interface ExportService {
    EximModel exportToExcel(TemplateExport templateExport, Object pCondition, boolean templateOnly);
}