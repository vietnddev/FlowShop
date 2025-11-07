package com.flowiee.pms.modules.system.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.common.security.UserSession;
import com.flowiee.pms.modules.staff.service.AccountService;
import com.flowiee.pms.common.enumeration.Pages;
import com.flowiee.pms.modules.staff.entity.Account;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RestController
@RequestMapping
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProfileControllerView extends BaseController {
	AccountService accountService;
	UserSession userSession;

	@GetMapping("/sys/profile")
	public ModelAndView showInformation(@ModelAttribute("message") String message) {
		Account lvAccount = userSession.getUserPrincipal().getEntity();
		ModelAndView modelAndView = new ModelAndView(Pages.SYS_PROFILE.getTemplate());
		modelAndView.addObject("message", message);
		modelAndView.addObject("profile", accountService.getMyProfile());
		modelAndView.addObject("listDonHangDaBan", new ArrayList<Order>());

		return baseView(modelAndView);
	}

	@PostMapping( "/sys/profile/update")
	public ModelAndView updateProfile(@ModelAttribute("account") Account pAccount) {
		accountService.updateProfile(pAccount);
		return new ModelAndView("redirect:/sys/profile");
	}

	@PostMapping("/sys/profile/change-password")
	public ModelAndView changePassword(HttpServletRequest request,
									   @ModelAttribute("account") Account accountEntity,
									   RedirectAttributes redirectAttributes) {
		String lvOldPassword = request.getParameter("oldPassword");
		String lvNewPassword = request.getParameter("newPassword");
		String lvRenewPassword = request.getParameter("renewPassword");

		if (CoreUtils.trim(lvNewPassword).equals(CoreUtils.trim(lvRenewPassword))) {
			accountService.changePassword(mvUserSession.getUserPrincipal().getId(), lvOldPassword, lvNewPassword);

			redirectAttributes.addAttribute("message", "Cập nhật thành công!");
			RedirectView redirectView = new RedirectView();
			redirectView.setUrl("/sys/profile");
			return new ModelAndView(redirectView);
		}

		redirectAttributes.addAttribute("message", "Updated failingly!");

		return new ModelAndView("redirect:/sys/profile");
	}
}