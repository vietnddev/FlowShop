package com.flowiee.pms.modules.sales.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.base.controller.ControllerHelper;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.system.model.EximResult;
import com.flowiee.pms.modules.sales.model.GeneralLedger;
import com.flowiee.pms.modules.system.service.ExportService;
import com.flowiee.pms.modules.sales.service.LedgerService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import com.flowiee.pms.common.enumeration.TemplateExport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("${app.api.prefix}/ledger")
@Tag(name = "LedgerPayment API", description = "Quản lý phiếu chi")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GeneralLedgerController extends BaseController {
    LedgerService mvLedgerService;
    @Qualifier("ledgerExportServiceImpl")
    @NonFinal
    @Autowired
    ExportService mvExportService;
    ControllerHelper mvCHelper;

    @Operation(summary = "Find general ledger")
    @GetMapping
    @PreAuthorize("@vldModuleSales.readGeneralLedger(true)")
    public AppResponse<GeneralLedger> findGeneralLedger(@RequestParam("pageSize") Integer pageSize,
                                                        @RequestParam("pageNum") Integer pageNum,
                                                        @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
                                                        @RequestParam(value = "toDate", required = false) LocalDate toDate) {
        try {
            GeneralLedger generalLedger = mvLedgerService.findGeneralLedger(pageSize, pageNum -1, fromDate, toDate);
            return mvCHelper.success(generalLedger, pageNum, pageSize, generalLedger.getTotalPages(), generalLedger.getTotalElements());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "generalLedger"), ex);
        }
    }

    @Operation(summary = "Export list transactions of general ledger")
    @GetMapping("/export")
    @PreAuthorize("@vldModuleSales.readGeneralLedger(true)")
    public ResponseEntity<InputStreamResource> exportData() {
        EximResult model = mvExportService.exportToExcel(TemplateExport.EX_LEDGER_TRANSACTIONS, null, false);
        return ResponseEntity.ok().headers(model.getHttpHeaders()).body(model.getContent());
    }
}