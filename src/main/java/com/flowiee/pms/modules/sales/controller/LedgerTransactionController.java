package com.flowiee.pms.modules.sales.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.base.controller.ControllerHelper;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.sales.dto.LedgerTransactionDTO;
import com.flowiee.pms.modules.sales.service.LedgerPaymentService;
import com.flowiee.pms.modules.sales.service.LedgerReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/ledger-trans")
@Tag(name = "Ledger transaction API", description = "Quản lý phiếu thu và phiếu chi")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LedgerTransactionController extends BaseController {
    LedgerReceiptService mvLedgerReceiptService;
    LedgerPaymentService mvLedgerPaymentService;
    ControllerHelper mvCHelper;

    @Operation(summary = "Find all trans receipts")
    @GetMapping("/receipt/all")
    @PreAuthorize("@vldModuleSales.readLedgerTransaction(true)")
    public AppResponse<List<LedgerTransactionDTO>> findLedgerReceipts(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                      @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        Page<LedgerTransactionDTO> ledgerReceipts = mvLedgerReceiptService.findAll(pageSize, pageNum - 1, null, null);
        return mvCHelper.success(ledgerReceipts.getContent(), pageNum, pageSize, ledgerReceipts.getTotalPages(), ledgerReceipts.getTotalElements());
    }

    @Operation(summary = "Find all trans payments")
    @GetMapping("/payment/all")
    @PreAuthorize("@vldModuleSales.readLedgerTransaction(true)")
    public AppResponse<List<LedgerTransactionDTO>> findLedgerPayments(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                   @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        Page<LedgerTransactionDTO> ledgerReceipts = mvLedgerPaymentService.findAll(pageSize, pageNum - 1, null, null);
        return mvCHelper.success(ledgerReceipts.getContent(), pageNum, pageSize, ledgerReceipts.getTotalPages(), ledgerReceipts.getTotalElements());
    }
}