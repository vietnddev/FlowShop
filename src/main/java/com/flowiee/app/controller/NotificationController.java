package com.flowiee.app.controller;

import com.flowiee.app.base.BaseController;
import com.flowiee.app.utils.PagesUtil;
import com.flowiee.app.entity.Notification;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/he-thong/notification")
public class NotificationController extends BaseController {
    @GetMapping
    public ModelAndView getAllNotification() {
        if (!accountService.isLogin()) {
            return new ModelAndView(PagesUtil.SYS_LOGIN);
        }
        ModelAndView modelAndView = new ModelAndView(PagesUtil.SYS_NOTIFICATION);
        modelAndView.addObject("notification", new Notification());        
        return baseView(modelAndView);        
    }
}