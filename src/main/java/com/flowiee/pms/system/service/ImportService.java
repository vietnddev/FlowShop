package com.flowiee.pms.system.service;

import com.flowiee.pms.system.model.EximResult;
import com.flowiee.pms.shared.enums.TemplateExport;
import org.springframework.web.multipart.MultipartFile;

public interface ImportService {
    EximResult importFromExcel(TemplateExport templateExport, MultipartFile multipartFile);

    String approveData();
}