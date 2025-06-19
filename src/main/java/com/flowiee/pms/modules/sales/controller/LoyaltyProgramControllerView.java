package com.flowiee.pms.modules.sales.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.modules.sales.entity.LoyaltyProgram;
import com.flowiee.pms.modules.sales.service.LoyaltyProgramService;
import com.flowiee.pms.common.enumeration.Pages;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/loyalty-programs")
@RequiredArgsConstructor
public class LoyaltyProgramControllerView extends BaseController {
    private final LoyaltyProgramService loyaltyProgramService;

    @GetMapping
    @PreAuthorize("@vldModuleSales.readLoyaltyProgram(true)")
    public ModelAndView getPrograms() {
        List<LoyaltyProgram> loyaltyProgramList = loyaltyProgramService.findAll();

        ModelAndView modelAndView = new ModelAndView(Pages.SLS_LOYALTY_PROGRAM.getTemplate());
        modelAndView.addObject("loyaltyProgramList", loyaltyProgramList);
        return baseView(modelAndView);
    }

    @GetMapping("/{programId}")
    @PreAuthorize("@vldModuleSales.readLoyaltyProgram(true)")
    public ModelAndView getDetailProgram(@PathVariable("programId") Long programId) {
        LoyaltyProgram loyaltyProgram = loyaltyProgramService.findById(programId, true);

        ModelAndView modelAndView = new ModelAndView(Pages.SLS_LOYALTY_PROGRAM_DETAIL.getTemplate());
        modelAndView.addObject("loyaltyProgram", loyaltyProgram);
        return baseView(modelAndView);
    }
}