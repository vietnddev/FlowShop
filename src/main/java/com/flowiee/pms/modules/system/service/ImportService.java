package com.flowiee.pms.modules.system.service;

import com.flowiee.pms.modules.system.model.EximResult;
import com.flowiee.pms.common.enumeration.TemplateExport;
import org.springframework.web.multipart.MultipartFile;

public interface ImportService {
    EximResult importFromExcel(TemplateExport templateExport, MultipartFile multipartFile);

    String approveData();
}