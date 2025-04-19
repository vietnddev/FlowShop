package com.flowiee.pms.modules.system.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.modules.system.service.BranchService;
import com.flowiee.pms.common.enumeration.Pages;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/sys/branch")
@Tag(name = "Branch API", description = "Quản lý danh sách chi nhánh")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BranchControllerView extends BaseController {
    BranchService branchService;

    @GetMapping
    @PreAuthorize("@vldModuleSystem.readBranch(true)")
    public ModelAndView findAllBranches() {
        ModelAndView modelAndView = new ModelAndView(Pages.SYS_BRANCH.getTemplate());
        modelAndView.addObject("branches", branchService.findAll());
        return baseView(modelAndView);
    }
}