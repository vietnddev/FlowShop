package com.flowiee.pms.modules.system.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.enumeration.ErrorCode;
import com.flowiee.pms.common.enumeration.TemplateExport;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.modules.system.entity.SystemLog;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.system.model.EximResult;
import com.flowiee.pms.modules.system.service.ExportService;
import com.flowiee.pms.modules.system.service.SystemLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/sys")
@Tag(name = "System API", description = "Quản lý hệ thống")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SystemLogController extends BaseController {
    SystemLogService logService;
    @Qualifier("logExportServiceImpl")
    @NonFinal
    @Autowired
    ExportService exportService;

    @Operation(summary = "Find all log")
    @GetMapping("/log/all")
    @PreAuthorize("@vldModuleSystem.readLog(true)")
    public AppResponse<List<SystemLog>> findLogs(@RequestParam("pageSize") int pageSize, @RequestParam("pageNum") int pageNum) {
        try {
            Page<SystemLog> logPage = logService.findAll(pageSize, pageNum - 1);
            return AppResponse.paged(logPage);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "system log"), ex);
        }
    }

    @Operation(summary = "Export all log")
    @GetMapping("/log/export")
    @PreAuthorize("@vldModuleSystem.readLog(true)")
    public ResponseEntity<InputStreamResource> exportToExcel() {
        EximResult model = exportService.exportToExcel(TemplateExport.EX_LIST_OF_LOGS, null, false);
        return ResponseEntity.ok().headers(model.getHttpHeaders()).body(model.getContent());
    }
}