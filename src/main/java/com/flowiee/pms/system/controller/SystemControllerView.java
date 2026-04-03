package com.flowiee.pms.system.controller;

import com.flowiee.pms.shared.base.BaseController;
import com.flowiee.pms.system.dto.AccountDTO;
import com.flowiee.pms.system.service.AccountService;
import com.flowiee.pms.system.dto.SystemConfigDTO;
import com.flowiee.pms.system.service.ConfigService;
import com.flowiee.pms.shared.enums.Pages;
import com.flowiee.pms.system.service.LanguageService;
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
    LanguageService languageService;

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
        modelAndView.addObject("listConfig", configService.getAll());
        return baseView(modelAndView);
    }

    @PostMapping("/config/update/{id}")
    @PreAuthorize("@vldModuleSystem.updateConfig(true)")
    public ModelAndView update(@ModelAttribute("config") SystemConfigDTO config, @PathVariable("id") Long configId) {
        configService.update(config, configId);
        return new ModelAndView("redirect:/he-thong/config");
    }

    @GetMapping("/language")
    @PreAuthorize("@vldModuleSystem.readConfig(true)")
    public ModelAndView showLanguages() {
        ModelAndView modelAndView = new ModelAndView(Pages.SYS_LANGUAGE.getTemplate());
        modelAndView.addObject("thLanguages", languageService.findAll(0, 10, null));
        return baseView(modelAndView);
    }

    @GetMapping("/data-temp")
    @PreAuthorize("@vldModuleSystem.readConfig(true)")
    public ModelAndView getDataCrawled() {
        return baseView(new ModelAndView(Pages.SYS_DATA_TEMP.getTemplate()));
    }
}