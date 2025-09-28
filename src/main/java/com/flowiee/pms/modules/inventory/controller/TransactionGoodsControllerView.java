package com.flowiee.pms.modules.inventory.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.enumeration.Pages;
import com.flowiee.pms.common.exception.ResourceNotFoundException;
import com.flowiee.pms.modules.inventory.dto.TransactionGoodsDTO;
import com.flowiee.pms.modules.inventory.enums.TransactionGoodsStatus;
import com.flowiee.pms.modules.inventory.enums.TransactionGoodsType;
import com.flowiee.pms.modules.inventory.service.TransactionGoodsService;
import com.flowiee.pms.modules.sales.dto.SupplierDTO;
import com.flowiee.pms.modules.sales.service.SupplierService;
import com.flowiee.pms.modules.staff.dto.AccountDTO;
import com.flowiee.pms.modules.staff.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/stg/transaction-goods")
@RequiredArgsConstructor
public class TransactionGoodsControllerView extends BaseController {
    private final TransactionGoodsService transactionGoodsService;
    private final SupplierService supplierService;
    private final AccountService accountService;

    @GetMapping("/import")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public ModelAndView displayImportTransactions() {
        return baseView(new ModelAndView(Pages.STG_TRANS_GOODS.getTemplate()));
    }

    @GetMapping("/import/{id}")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public ModelAndView displayImportTransactionDetail(@PathVariable("id") Long pTranId) {
        TransactionGoodsDTO transactionGoods = transactionGoodsService.findDtoById(pTranId, false);
        if (transactionGoods == null || !TransactionGoodsType.IMPORT.equals(transactionGoods.getTransactionType())) {
            throw new ResourceNotFoundException("Transaction goods invalid!", null, null, true);
        }

        List<SupplierDTO> suppliers = supplierService.findAll(-1, -1, List.of()).getContent();
        List<AccountDTO> staffs = accountService.find();
        List<TransactionGoodsStatus> statuses = List.of(TransactionGoodsStatus.values());

        ModelAndView modelAndView = new ModelAndView(Pages.STG_TRANS_GOODS_DETAIL.getTemplate());
        modelAndView.addObject("thTransactionGoods", transactionGoods);
        modelAndView.addObject("thSuppliers", suppliers);
        modelAndView.addObject("thStaffs", staffs);
        modelAndView.addObject("thStatus", statuses);
        return baseView(modelAndView);
    }

    @GetMapping("/export")
    @PreAuthorize("@vldModuleSales.exportGoods(true)")
    public ModelAndView displayExportTransactions() {
        return baseView(new ModelAndView(Pages.STG_TRANS_EXPORT.getTemplate()));
    }

    @GetMapping("/export/{id}")
    @PreAuthorize("@vldModuleSales.exportGoods(true)")
    public ModelAndView displayExportTransactionDetail(@PathVariable("id") Long pTranId) {
        TransactionGoodsDTO transactionGoods = transactionGoodsService.findDtoById(pTranId, false);
        if (transactionGoods == null || !TransactionGoodsType.EXPORT.equals(transactionGoods.getTransactionType())) {
            throw new ResourceNotFoundException("Transaction goods invalid!", null, null, true);
        }

        List<SupplierDTO> suppliers = supplierService.findAll(-1, -1, List.of()).getContent();
        List<AccountDTO> staffs = accountService.find();
        List<TransactionGoodsStatus> statuses = List.of(TransactionGoodsStatus.values());

        ModelAndView modelAndView = new ModelAndView(Pages.STG_TRANS_GOODS_DETAIL.getTemplate());
        modelAndView.addObject("thTransactionGoods", transactionGoods);
        modelAndView.addObject("thSuppliers", suppliers);
        modelAndView.addObject("thStaffs", staffs);
        modelAndView.addObject("thStatus", statuses);
        return baseView(modelAndView);
    }
}