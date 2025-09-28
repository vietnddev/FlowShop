package com.flowiee.pms.modules.inventory.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.enumeration.Pages;
import com.flowiee.pms.modules.inventory.enums.TransactionGoodsStatus;
import com.flowiee.pms.modules.sales.dto.SupplierDTO;
import com.flowiee.pms.modules.sales.service.SupplierService;
import com.flowiee.pms.modules.staff.dto.AccountDTO;
import com.flowiee.pms.modules.staff.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/stg/transaction-goods")
@RequiredArgsConstructor
public class TransactionGoodsControllerView extends BaseController {
    private final SupplierService supplierService;
    private final AccountService accountService;

    @GetMapping("/import/create")
    public ModelAndView displayGoodsImportForm() {
        List<SupplierDTO> suppliers = supplierService.findAll(-1, -1, List.of()).getContent();
        List<AccountDTO> staffs = accountService.find();
        List<TransactionGoodsStatus> statuses = List.of(TransactionGoodsStatus.values());

        ModelAndView modelAndView = new ModelAndView(Pages.STG_GOODS_IMPORT_CREATION.getTemplate());
        modelAndView.addObject("thSuppliers", suppliers);
        modelAndView.addObject("thStaffs", staffs);
        modelAndView.addObject("thStatus", statuses);
        return baseView(modelAndView);

    }
}