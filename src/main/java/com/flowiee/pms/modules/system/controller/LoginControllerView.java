package com.flowiee.pms.modules.system.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class LoginControllerView {
    @Value("${system.login.bypass}")
    private boolean bypass;

    @GetMapping( "/sys/login")
    public ModelAndView showLoginPage() {
        return new ModelAndView("login").addObject("byPass", bypass);
    }
}