package com.flowiee.pms.modules.system.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.exception.ErrorModel;
import com.flowiee.pms.common.enumeration.Pages;
import com.flowiee.pms.common.enumeration.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/error")
public class ErrorController extends BaseController {
    @GetMapping("/403")
    public ModelAndView forbiddenException() {
        ErrorModel error = new ErrorModel(HttpStatus.FORBIDDEN.value(), ErrorCode.ERROR_FORBIDDEN.getDescription());
        ModelAndView modelAndView = new ModelAndView(Pages.SYS_ERROR.getTemplate());
        modelAndView.addObject("error", error);
        return baseView(modelAndView);
    }

    @GetMapping("/404")
    public ModelAndView notfoundException() {
        ErrorModel error = new ErrorModel(HttpStatus.FORBIDDEN.value(), ErrorCode.RESOURCE_NOT_FOUND.getDescription());
        ModelAndView modelAndView = new ModelAndView(Pages.SYS_ERROR.getTemplate());
        modelAndView.addObject("error", error);
        return baseView(modelAndView);
    }
}