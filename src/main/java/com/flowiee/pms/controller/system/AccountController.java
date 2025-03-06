package com.flowiee.pms.controller.system;

import com.flowiee.pms.base.BaseController;
import com.flowiee.pms.entity.system.Account;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.DataExistsException;
import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.model.role.RoleModel;
import com.flowiee.pms.service.system.AccountService;
import com.flowiee.pms.service.system.RoleService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/system/account")
@Tag(name = "Account system API", description = "Quản lý tài khoản hệ thống")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountController extends BaseController {
    RoleService    roleService;
    AccountService accountService;

    @Operation(summary = "Find all accounts")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleSystem.readAccount(true)")
    public AppResponse<List<Account>> findAllAccounts() {
        try {
            return success(accountService.findAll());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "account"), ex);
        }
    }

    @Operation(summary = "Find detail account")
    @GetMapping("/{accountId}")
    @PreAuthorize("@vldModuleSystem.readAccount(true)")
    public AppResponse<Account> findDetailAccount(@PathVariable("accountId") Long accountId) {
        return success(accountService.findById(accountId, true));
    }

    @Operation(summary = "Create account")
    @PostMapping(value = "/create")
    @PreAuthorize("@vldModuleSystem.insertAccount(true)")
    public AppResponse<Account> save(@RequestBody Account account) {
        try {
            if (account == null) {
                throw new BadRequestException();
            }
            if (accountService.findByUsername(account.getUsername()) != null) {
                throw new DataExistsException("Username exists!");
            }
            return success(accountService.save(account));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "account"), ex);
        }
    }

    @Operation(summary = "Update account")
    @PutMapping(value = "/update/{accountId}")
    @PreAuthorize("@vldModuleSystem.updateAccount(true)")
    public AppResponse<Account> update(@RequestBody Account account, @PathVariable("accountId") Long accountId) {
        return success(accountService.update(account, accountId));
    }

    @PutMapping("/update-permission/{accountId}")
    @PreAuthorize("@vldModuleSystem.updateAccount(true)")
    public AppResponse<List<RoleModel>> updatePermission(@RequestBody String[] actions, @PathVariable("accountId") Long accountId) {
        try {
            if (accountId <= 0 || accountService.findById(accountId, true) == null) {
                throw new BadRequestException();
            }
//            roleService.deleteAllRole(accountId);
//            List<ActionOfModule> listAction = roleService.findAllAction();
//            for (ActionOfModule sysAction : listAction) {
//                String clientActionKey = request.getParameter(sysAction.getActionKey());
//                if (clientActionKey != null) {
//                    boolean isAuthorSelected = clientActionKey.equals("on");
//                    if (isAuthorSelected) {
//                        roleService.updatePermission(sysAction.getModuleKey(), sysAction.getActionKey(), accountId);
//                    }
//                }
//            }
            return success(null);
        } catch (RuntimeException ex) {
            throw new AppException(ex);
        }
    }

    @Operation(summary = "Delete account")
    @DeleteMapping(value = "/delete/{accountId}")
    @PreAuthorize("@vldModuleSystem.deleteAccount(true)")
    public AppResponse<String> deleteAccount(@PathVariable("accountId") Long accountId) {
        return success(accountService.delete(accountId));
    }

    @Operation(summary = "Find roles of account")
    @GetMapping("/{accountId}/role")
    @PreAuthorize("@vldModuleSystem.readAccount(true)")
    public AppResponse<List<RoleModel>> findRolesOfAccount(@PathVariable("accountId") Long accountId) {
        try {
            return success(roleService.findAllRoleByAccountId(accountId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "role"), ex);
        }
    }
}