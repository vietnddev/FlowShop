package com.flowiee.pms.modules.sales.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.modules.sales.dto.LoyaltyProgramDTO;
import com.flowiee.pms.modules.sales.entity.LoyaltyProgram;
import com.flowiee.pms.modules.sales.service.LoyaltyProgramService;
import com.flowiee.pms.common.enumeration.Pages;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/sls/loyalty-programs")
@RequiredArgsConstructor
public class LoyaltyProgramControllerView extends BaseController {
    private final LoyaltyProgramService mvloyaltyProgramService;

    @GetMapping
    @PreAuthorize("@vldModuleSales.readLoyaltyProgram(true)")
    public ModelAndView getPrograms() {
        List<LoyaltyProgram> loyaltyProgramList = mvloyaltyProgramService.find();

        ModelAndView modelAndView = new ModelAndView(Pages.SLS_LOYALTY_PROGRAM.getTemplate());
        modelAndView.addObject("loyaltyPrograms", loyaltyProgramList);
        modelAndView.addObject("loyaltyProgram", new LoyaltyProgramDTO());
        return baseView(modelAndView);
    }

    @GetMapping("/{programId}")
    @PreAuthorize("@vldModuleSales.readLoyaltyProgram(true)")
    public ModelAndView getDetailProgram(@PathVariable("programId") Long programId) {
        LoyaltyProgramDTO loyaltyProgram = mvloyaltyProgramService.findById(programId, true);

        ModelAndView modelAndView = new ModelAndView(Pages.SLS_LOYALTY_PROGRAM_DETAIL.getTemplate());
        modelAndView.addObject("loyaltyProgram", loyaltyProgram);
        return baseView(modelAndView);
    }

    @PostMapping
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public ModelAndView createLoyaltyProgram(@Valid @ModelAttribute LoyaltyProgramDTO pRequest,
                                             BindingResult  bindingResult) {
        mvloyaltyProgramService.save(pRequest);
        return new ModelAndView("redirect:/sls/loyalty-programs");
    }
}