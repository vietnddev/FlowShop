package com.flowiee.pms.modules.staff.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.modules.staff.service.GroupAccountService;
import com.flowiee.pms.common.enumeration.Pages;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/sys/group-account")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GroupAccountControllerView extends BaseController {
    GroupAccountService groupAccountService;

    @GetMapping
    @PreAuthorize("@vldModuleSystem.readGroupAccount(true)")
    public ModelAndView findAllGroup() {
        return baseView(new ModelAndView(Pages.SYS_GR_ACC.getTemplate()));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("@vldModuleSystem.readGroupAccount(true)")
    public ModelAndView findDetailGroup(@PathVariable("id") Long groupId) {
        ModelAndView modelAndView = new ModelAndView(Pages.SYS_GR_ACC_DETAIL.getTemplate());
        modelAndView.addObject("groupAccount", groupAccountService.findById(groupId, true));
        return baseView(modelAndView);
    }
}