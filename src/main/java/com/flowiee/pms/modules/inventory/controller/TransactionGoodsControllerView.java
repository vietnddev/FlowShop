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

    @GetMapping("/{tranType}")//tranType is `import` or `export`
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public ModelAndView displayImportTransactions(@PathVariable("tranType") String pTranType) {
        TransactionGoodsType lvTranType;
        try {
            lvTranType = TransactionGoodsType.fromStr(pTranType);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Transaction type invalid!", null, null, true);
        }

        Pages lvPage = switch (lvTranType) {
            case IMPORT, EXPORT -> Pages.STG_TRANS_GOODS;
        };

        ModelAndView modelAndView = baseView(new ModelAndView(lvPage.getTemplate()));
        modelAndView.addObject("thTransactionType", lvTranType.name().toLowerCase());

        return modelAndView;
    }

    @GetMapping("/{tranType}/{id}")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public ModelAndView displayImportTransactionDetail(@PathVariable("tranType") String pTranType, @PathVariable("id") Long pTranId) {
        TransactionGoodsDTO lvTransactionGoods = transactionGoodsService.findDtoById(pTranId, false);
        if (lvTransactionGoods == null) {
            throw new ResourceNotFoundException("Transaction goods invalid!", null, null, true);
        }

        TransactionGoodsType lvRequestTranType;
        try {
            lvRequestTranType = TransactionGoodsType.fromStr(pTranType);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Transaction type invalid!", null, null, true);
        }

        TransactionGoodsType lvTranType = lvTransactionGoods.getTransactionType();
        ModelAndView modelAndView = new ModelAndView();

        switch (lvRequestTranType) {
            case IMPORT -> {
                if (!TransactionGoodsType.IMPORT.equals(lvTranType)) {
                    throw new ResourceNotFoundException("Transaction import goods invalid!", null, null, true);
                }
                modelAndView.setViewName(Pages.STG_TRANS_GOODS_DETAIL.getTemplate());
            }
            case EXPORT -> {
                if (!TransactionGoodsType.EXPORT.equals(lvTranType)) {
                    throw new ResourceNotFoundException("Transaction export goods invalid!", null, null, true);
                }
                modelAndView.setViewName(Pages.STG_TRANS_GOODS_DETAIL.getTemplate());
            }
        }

        List<SupplierDTO> suppliers = supplierService.findAll(-1, -1, List.of()).getContent();
        List<AccountDTO> staffs = accountService.find();
        List<TransactionGoodsStatus> statuses = List.of(TransactionGoodsStatus.values());

        modelAndView.addObject("thTransactionGoods", lvTransactionGoods);
        modelAndView.addObject("thSuppliers", suppliers);
        modelAndView.addObject("thStaffs", staffs);
        modelAndView.addObject("thStatus", statuses);

        return baseView(modelAndView);
    }
}