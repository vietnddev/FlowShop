package com.flowiee.pms.controller.system;

import com.flowiee.pms.base.BaseController;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.DataExistsException;
import com.flowiee.pms.model.dto.AccountDTO;
import com.flowiee.pms.service.system.*;
import com.flowiee.pms.common.enumeration.AccountStatus;
import com.flowiee.pms.common.enumeration.Pages;
import com.flowiee.pms.entity.system.Account;
import com.flowiee.pms.model.role.ActionModel;
import com.flowiee.pms.model.role.RoleModel;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys/tai-khoan")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountControllerView extends BaseController {
    RoleService         roleService;
    BranchService       branchService;
    AccountService      accountService;
    GroupAccountService groupAccountService;
    ResetPasswordService resetPasswordService;

    @GetMapping
    @PreAuthorize("@vldModuleSystem.readAccount(true)")
    public ModelAndView findAllAccount() {
        ModelAndView modelAndView = new ModelAndView(Pages.SYS_ACCOUNT.getTemplate());
        modelAndView.addObject("account", new Account());
        modelAndView.addObject("listAccount", accountService.findAll());
        modelAndView.addObject("groupAccount", groupAccountService.findAll());
        modelAndView.addObject("listBranch", branchService.findAll());
        return baseView(modelAndView);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("@vldModuleSystem.readAccount(true)")
    public ModelAndView findDetailAccountById(@PathVariable("id") Long accountId) {
        Account account = accountService.findById(accountId, true);
        List<RoleModel> roleOfAccount = roleService.findAllRoleByAccountId(accountId);
        ModelAndView modelAndView = new ModelAndView(Pages.SYS_ACCOUNT_DETAIL.getTemplate());
        modelAndView.addObject("listRole", roleOfAccount);
        modelAndView.addObject("accountInfo", AccountDTO.toDTO(account));
        modelAndView.addObject("groupAccount", groupAccountService.findAll());
        modelAndView.addObject("listBranch", branchService.findAll());

        return baseView(modelAndView);
    }

    @PostMapping(value = "/insert")
    @PreAuthorize("@vldModuleSystem.insertAccount(true)")
    public ModelAndView save(@ModelAttribute("account") Account account) {
        if (accountService.findByUsername(account.getUsername()) != null) {
            throw new DataExistsException("Username exists!");
        }
        accountService.save(account);
        return new ModelAndView("redirect:/sys/tai-khoan");
    }

    @PostMapping(value = "/update/{id}")
    @PreAuthorize("@vldModuleSystem.updateAccount(true)")
    public ModelAndView update(@ModelAttribute("account") Account accountEntity,
                               @PathVariable("id") Long accountId,
                               HttpServletRequest request) {
        Account account = accountService.findById(accountId, true);
        //warning -> need to in the feature
        accountService.update(accountEntity, accountId);
        return refreshPage(request);
    }

    @PostMapping(value = "/delete/{id}")
    @PreAuthorize("@vldModuleSystem.deleteAccount(true)")
    public ModelAndView deleteAccount(@PathVariable("id") Long accountId) {
        Account account = accountService.findById(accountId, true);
        account.setStatus(AccountStatus.C.name());
        accountService.save(account);
        return new ModelAndView("redirect:/sys/tai-khoan");
    }

    @PostMapping("/update-permission/{id}")
    @PreAuthorize("@vldModuleSystem.updateAccount(true)")
    public ModelAndView updatePermission(@PathVariable("id") Long accountId, HttpServletRequest request) {
        Account lvAccount = accountService.findById(accountId, true);
        List<ActionModel> lvRightsSelected = roleService.findAllAction().stream()
                .filter(sysAction -> {
                    String lvRights = CoreUtils.trim(request.getParameter(sysAction.getActionKey()));
                    return "on".equals(lvRights);
                })
                .collect(Collectors.toList());
        roleService.updatePermission(lvAccount, lvRightsSelected);

        return new ModelAndView("redirect:/sys/tai-khoan/" + accountId);
    }

    @GetMapping(value = "/reset-password/{accountId}")
    public ModelAndView requestResetPassword(@PathVariable("accountId") long accountId, HttpSession session, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
        Account account = accountService.findById(accountId, true);
        if (account.getEmail() != null) {
            if (resetPasswordService.sendToken(account.getEmail(), request)) {
                session.setAttribute("successMsg", "Please check your email, password reset link has been sent to your email.");
            } else {
                session.setAttribute("errorMsg", "Something wrong on server. Email Not Sent!");
            }
        } else {
            throw new BadRequestException("Invalid Email");
        }
        return refreshPage(request);
    }
}