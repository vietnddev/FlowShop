package com.flowiee.pms.service;

import com.flowiee.pms.model.EximResult;
import com.flowiee.pms.common.enumeration.TemplateExport;
import org.springframework.web.multipart.MultipartFile;

public interface ImportService {
    EximResult importFromExcel(TemplateExport templateExport, MultipartFile multipartFile);
}