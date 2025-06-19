package com.flowiee.pms.modules.sales.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.exception.ResourceNotFoundException;
import com.flowiee.pms.modules.sales.dto.LedgerTransactionDTO;
import com.flowiee.pms.modules.system.service.CategoryService;
import com.flowiee.pms.modules.sales.service.LedgerPaymentService;
import com.flowiee.pms.modules.sales.service.LedgerReceiptService;
import com.flowiee.pms.common.enumeration.CATEGORY;
import com.flowiee.pms.common.enumeration.Pages;
import com.flowiee.pms.common.enumeration.LedgerTranType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/ledger/trans/")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LedgerTransactionControllerView extends BaseController {
    LedgerReceiptService mvLedgerReceiptService;
    LedgerPaymentService mvLedgerPaymentService;
    CategoryService mvCategoryService;

    @GetMapping("/receipt")
    @PreAuthorize("@vldModuleSales.readLedgerTransaction(true)")
    public ModelAndView getLedgerReceipts() {
        setupSearchTool(true, List.of(CATEGORY.PAYMENT_METHOD.name()));
        ModelAndView modelAndView = new ModelAndView(Pages.SLS_LEDGER_TRANS.getTemplate());
        modelAndView.addObject("tranTypeKey", LedgerTranType.PT.name());
        modelAndView.addObject("tranTypeName", LedgerTranType.PT.getDescription());
        this.getCategoryOfTransaction(modelAndView, LedgerTranType.PT.name());
        return baseView(modelAndView);
    }

    @GetMapping("/payment")
    @PreAuthorize("@vldModuleSales.readLedgerTransaction(true)")
    public ModelAndView getLedgerPayments() {
        setupSearchTool(true, List.of(CATEGORY.PAYMENT_METHOD.name()));
        ModelAndView modelAndView = new ModelAndView(Pages.SLS_LEDGER_TRANS.getTemplate());
        modelAndView.addObject("tranTypeKey", LedgerTranType.PC.name());
        modelAndView.addObject("tranTypeName", LedgerTranType.PC.getDescription());
        this.getCategoryOfTransaction(modelAndView, LedgerTranType.PC.name());
        return baseView(modelAndView);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@vldModuleSales.readLedgerTransaction(true)")
    public ModelAndView findTransactionDetail(@PathVariable("id") Long tranId) {
        LedgerTransactionDTO transaction = mvLedgerReceiptService.findById(tranId, false);
        if (transaction == null) {
            throw new ResourceNotFoundException("Ledger receipt not found!");
        }
        ModelAndView modelAndView = new ModelAndView(Pages.SLS_LEDGER_TRANS_DETAIL.getTemplate());
        modelAndView.addObject("tranId", tranId);
        modelAndView.addObject("ledgerTransactionDetail", transaction);
        return baseView(modelAndView);
    }

    @PostMapping("/receipt/insert")
    @PreAuthorize("@vldModuleSales.insertLedgerTransaction(true)")
    public ModelAndView insertLedgerReceipt(LedgerTransactionDTO transaction) {
        mvLedgerReceiptService.save(transaction);
        return new ModelAndView("redirect:/ledger/trans/receipt");
    }

    @PostMapping("/payment/insert")
    @PreAuthorize("@vldModuleSales.insertLedgerTransaction(true)")
    public ModelAndView insertLedgerPayment(LedgerTransactionDTO transaction) {
        mvLedgerPaymentService.save(transaction);
        return new ModelAndView("redirect:/ledger/trans/payment");
    }

    private void getCategoryOfTransaction(ModelAndView modelAndView, String tranType) {
        modelAndView.addObject("listGroupObjects", mvCategoryService.findLedgerGroupObjects());
        modelAndView.addObject("listPaymentMethods", mvCategoryService.findPaymentMethods());
        if (LedgerTranType.PT.name().equals(tranType)) {
            modelAndView.addObject("listTranContents", mvCategoryService.findLedgerReceiptTypes());
        } else if (LedgerTranType.PC.name().equals(tranType)) {
            modelAndView.addObject("listTranContents", mvCategoryService.findLedgerPaymentTypes());
        }
    }
}