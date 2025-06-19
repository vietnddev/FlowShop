package com.flowiee.pms.modules.inventory.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.modules.inventory.dto.TicketExportDTO;
import com.flowiee.pms.modules.inventory.entity.TicketExport;
import com.flowiee.pms.common.enumeration.Pages;
import com.flowiee.pms.modules.inventory.service.TicketExportService;
import com.flowiee.pms.common.enumeration.TicketExportStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@RequestMapping("/stg/ticket-export")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TicketExportControllerView extends BaseController {
    TicketExportService mvTicketExportService;

    @GetMapping
    @PreAuthorize("@vldModuleSales.exportGoods(true)")
    public ModelAndView viewAllTicketExport() {
        ModelAndView modelAndView = new ModelAndView(Pages.STG_TICKET_EXPORT.getTemplate());
        modelAndView.addObject("listTicketExport", mvTicketExportService.findAll(-1, -1, null));
        return baseView(modelAndView);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@vldModuleSales.exportGoods(true)")
    public ModelAndView viewDetail(@PathVariable("id") Long ticketExportId) {
        TicketExportDTO ticketExport = mvTicketExportService.findById(ticketExportId, true);
        LinkedHashMap<String, String> ticketExportStatus = new LinkedHashMap<>();
        ticketExportStatus.put(ticketExport.getStatus(), TicketExportStatus.valueOf(ticketExport.getStatus()).getLabel());
        if (TicketExportStatus.DRAFT.name().equals(ticketExport.getStatus())) {
            ticketExportStatus.put(TicketExportStatus.COMPLETED.name(), TicketExportStatus.COMPLETED.getLabel());
            ticketExportStatus.put(TicketExportStatus.CANCEL.name(), TicketExportStatus.CANCEL.getLabel());
        }
        ModelAndView modelAndView = new ModelAndView(Pages.STG_TICKET_EXPORT_DETAIL.getTemplate());
        modelAndView.addObject("ticketExportId", ticketExportId);
        modelAndView.addObject("ticketExportStatus", ticketExportStatus);
        modelAndView.addObject("ticketExportDetail", ticketExport);
        return baseView(modelAndView);
    }
}