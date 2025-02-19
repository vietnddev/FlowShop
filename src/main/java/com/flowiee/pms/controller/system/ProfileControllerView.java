package com.flowiee.pms.controller.system;

import com.flowiee.pms.base.controller.BaseController;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.service.system.AccountService;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.enumeration.Pages;
import com.flowiee.pms.entity.system.Account;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

	@GetMapping("/sys/profile")
	public ModelAndView showInformation(@ModelAttribute("message") String message) {
		Account profile = accountService.findById(CommonUtils.getUserPrincipal().getId(), true);

		ModelAndView modelAndView = new ModelAndView(Pages.SYS_PROFILE.getTemplate());
		modelAndView.addObject("message", message);
		modelAndView.addObject("profile", profile);
		modelAndView.addObject("listDonHangDaBan", new ArrayList<Order>());

		return baseView(modelAndView);
	}

	@PostMapping( "/sys/profile/update")
	public ModelAndView updateProfile(@ModelAttribute("account") Account pAccount) {
		Account account = accountService.findByUsername(pAccount.getUsername());
		account.setPhoneNumber(pAccount.getPhoneNumber());
		account.setEmail(pAccount.getEmail());
		account.setAddress(pAccount.getAddress());
		accountService.update(account, account.getId());

		return new ModelAndView("redirect:/profile");
	}

	@PostMapping("/sys/profile/change-password")
	public ModelAndView changePassword(HttpServletRequest request,
									   @ModelAttribute("account") Account accountEntity,
									   RedirectAttributes redirectAttributes) {
		String password_old = request.getParameter("password_old");
		String password_new = request.getParameter("password_new");
		String password_renew = request.getParameter("password_renew");

		Account profile = accountService.findById(CommonUtils.getUserPrincipal().getId(), true);

		BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
		if (bCrypt.matches(password_old, accountService.findByUsername(profile.getUsername()).getPassword())) {
			if (password_new.equals(password_renew)) {
				profile.setPassword(bCrypt.encode(password_new));
				accountService.save(accountEntity);

				redirectAttributes.addAttribute("message", "Cập nhật thành công!");
				RedirectView redirectView = new RedirectView();
				redirectView.setUrl("/profile");
				return new ModelAndView(redirectView);
			}
			redirectAttributes.addAttribute("message", "Mật khẩu nhập lại chưa khớp!");
		}
		redirectAttributes.addAttribute("message", "Sai mật khẩu hiện tại!");

		return new ModelAndView("redirect:/profile");
	}
}