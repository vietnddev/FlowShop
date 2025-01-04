package com.flowiee.pms.controller.system;

import com.flowiee.pms.base.controller.BaseController;
import com.flowiee.pms.service.system.ResetPasswordService;
import com.flowiee.pms.common.enumeration.Pages;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
public class HomeControllerView extends BaseController {
    private final ResetPasswordService mvResetPasswordService;

    @GetMapping(value = "/change-password")
    public ModelAndView showPageChangePassword() {
        return new ModelAndView(Pages.SYS_UNAUTHORIZED.getTemplate());
    }

    @GetMapping(value = "/change-password", params = "submit")
    public void submitChangePassword() {
        //
    }

    @GetMapping(value = "/forgot-password")
    public void showPageForgotPassword() {
        //Send password to email register
    }

    @GetMapping(value = "/reset-password")
    public ModelAndView requestResetPassword(@RequestParam("email") String pEmail, HttpSession session, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
        if(!ObjectUtils.isEmpty(pEmail)) {
            if (mvResetPasswordService.sendToken(pEmail, request)) {
                session.setAttribute("successMsg", "Please check your email, password reset link has been sent to your email.");
            } else {
                session.setAttribute("errorMsg", "Something wrong on server. Email Not Sent!");
            }
        } else {
            session.setAttribute("errorMsg", "Invalid Email");
        }
        return new ModelAndView("redirect:/forgot-password");
    }

    @PostMapping("/reset-password")
    public void doResetPassword(@RequestParam String token, @RequestParam String password,
                               HttpServletRequest request, HttpServletResponse response,
                               HttpSession session, Model model) throws IOException {
        if (mvResetPasswordService.resetPasswordWithToken(token, password)) {
            session.setAttribute("successMsg", "Password Changed Successfully");
            model.addAttribute("msg", "Password Changed Successfully");
            response.sendRedirect(request.getServletPath());
        } else {
            model.addAttribute("msg", "Your Link is invalid or expired!");
            String currentUrl = request.getRequestURL().toString();
            response.sendRedirect(currentUrl);
        }
    }
}