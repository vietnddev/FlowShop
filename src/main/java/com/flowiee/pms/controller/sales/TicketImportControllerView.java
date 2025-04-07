package com.flowiee.pms.controller.sales;

import com.flowiee.pms.entity.sales.TicketImport;
import com.flowiee.pms.exception.ResourceNotFoundException;
import com.flowiee.pms.model.dto.SupplierDTO;
import com.flowiee.pms.service.product.MaterialService;
import com.flowiee.pms.service.product.ProductVariantService;
import com.flowiee.pms.service.sales.SupplierService;
import com.flowiee.pms.service.sales.TicketImportService;
import com.flowiee.pms.service.storage.StorageService;
import com.flowiee.pms.base.BaseController;

import com.flowiee.pms.common.enumeration.Pages;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@RequestMapping("/stg/ticket-import")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TicketImportControllerView extends BaseController {
    StorageService mvStorageService;
    MaterialService mvMaterialService;
    SupplierService mvSupplierService;
    TicketImportService mvTicketImportService;
    ProductVariantService mvProductVariantService;

    @GetMapping
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public ModelAndView viewTickets() {
        ModelAndView modelAndView = new ModelAndView(Pages.STG_TICKET_IMPORT.getTemplate());
        modelAndView.addObject("listStorages", mvStorageService.findAll());
        return baseView(modelAndView);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public ModelAndView viewDetail(@PathVariable("id") Long ticketImportId) {
        TicketImport ticketImport = mvTicketImportService.findById(ticketImportId, true);

        List<SupplierDTO> suppliers = new ArrayList<>();
        if (ticketImport.getSupplier() != null) {
            suppliers.add(new SupplierDTO(ticketImport.getSupplier().getId(), ticketImport.getSupplier().getName()));
        } else {
            suppliers.add(new SupplierDTO(-1l, "Chọn nhà cung cấp"));
        }
        suppliers.addAll(mvSupplierService.findAll(-1, -1, ticketImport.getSupplier() != null ? List.of(ticketImport.getSupplier().getId()) : null).getContent());

        ModelAndView modelAndView = new ModelAndView(Pages.STG_TICKET_IMPORT_DETAIL.getTemplate());
        modelAndView.addObject("ticketImportId", ticketImportId);
        modelAndView.addObject("ticketImportDetail", ticketImport);
        modelAndView.addObject("listProductVariant", mvProductVariantService.findAll());
        modelAndView.addObject("listMaterial", mvMaterialService.findAll());
        modelAndView.addObject("listSupplier", suppliers);
        modelAndView.addObject("listStorage", mvStorageService.findAll());
        return baseView(modelAndView);
    }

    @ResponseBody
    @GetMapping("/search")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public void search() {
        List<TicketImport> data = mvTicketImportService.findAll();
        if (data != null) {
            for (TicketImport o : data) {
                System.out.println(o.toString());
            }
        }
    }

//    @GetMapping("/update/{id}")
//    public ModelAndView update(@ModelAttribute("goodsImport") TicketImport ticketImport,
//                         @PathVariable("id") Integer importId,
//                         HttpServletRequest request) {
//        validateModuleStorage.importGoods(true);
//        if (importId <= 0 || ticketImportService.findById(importId) == null) {
//            throw new NotFoundException("Goods import not found!");
//        }
//        ticketImportService.update(ticketImport, importId);
//        return new ModelAndView("redirect:" + request.getHeader("referer"));
//    }

    @GetMapping("/reset/{id}")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public ModelAndView clear(@PathVariable("id") Long draftImportId) {
        if (draftImportId <= 0 || mvTicketImportService.findById(draftImportId, true) == null) {
            throw new ResourceNotFoundException("Goods import not found!");
        }
        mvTicketImportService.delete(draftImportId);
        return new ModelAndView("redirect:");
    }

    @PostMapping("/send-approval/{id}")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public ModelAndView sendApproval(@PathVariable("id") Long importId) {
        if (importId <= 0 || mvTicketImportService.findById(importId, true) == null) {
            throw new ResourceNotFoundException("Goods import not found!");
        }
        mvTicketImportService.updateStatus(importId, "");
        return new ModelAndView("redirect:");
    }

    @PostMapping("/approve/{id}")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public ModelAndView approve(@PathVariable("id") Long importId) {
        TicketImport ticketImport = mvTicketImportService.findById(importId, true);
        mvTicketImportService.updateStatus(ticketImport.getId(), "");
        return new ModelAndView("redirect:");
    }
}