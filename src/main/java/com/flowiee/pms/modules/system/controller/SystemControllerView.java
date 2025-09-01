package com.flowiee.pms.modules.system.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.exception.ResourceNotFoundException;
import com.flowiee.pms.modules.staff.dto.AccountDTO;
import com.flowiee.pms.modules.staff.service.AccountService;
import com.flowiee.pms.modules.system.dto.SystemConfigDTO;
import com.flowiee.pms.modules.system.service.ConfigService;
import com.flowiee.pms.common.enumeration.Pages;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/sys")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SystemControllerView extends BaseController {
    ConfigService configService;
    AccountService accountService;

    @GetMapping("/notification")
    public ModelAndView getAllNotification() {
        return baseView(new ModelAndView(Pages.SYS_NOTIFICATION.getTemplate()));
    }

    @GetMapping("/log")
    @PreAuthorize("@vldModuleSystem.readLog(true)")
    public ModelAndView showPageLog() {
        List<AccountDTO> lvAccounts = accountService.find();
        ModelAndView modelAndView = new ModelAndView(Pages.SYS_LOG.getTemplate());
        modelAndView.addObject("actors", lvAccounts);
        return baseView(modelAndView);
    }

    @GetMapping("/config")
    @PreAuthorize("@vldModuleSystem.readConfig(true)")
    public ModelAndView showConfig() {
        ModelAndView modelAndView = new ModelAndView(Pages.SYS_CONFIG.getTemplate());
        modelAndView.addObject("listConfig", configService.find());
        return baseView(modelAndView);
    }

    @PostMapping("/config/update/{id}")
    @PreAuthorize("@vldModuleSystem.updateConfig(true)")
    public ModelAndView update(@ModelAttribute("config") SystemConfigDTO config, @PathVariable("id") Long configId) {
        if (configId <= 0 || configService.findById(configId).isEmpty()) {
            throw new ResourceNotFoundException("Config not found!");
        }
        configService.update(config, configId);
        return new ModelAndView("redirect:/he-thong/config");
    }

    @GetMapping("/data-temp")
    @PreAuthorize("@vldModuleSystem.readConfig(true)")
    public ModelAndView getDataCrawled() {
        return baseView(new ModelAndView(Pages.SYS_DATA_TEMP.getTemplate()));
    }
}