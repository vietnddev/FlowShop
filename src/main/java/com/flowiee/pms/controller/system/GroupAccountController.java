package com.flowiee.pms.controller.system;

import com.flowiee.pms.base.controller.BaseController;
import com.flowiee.pms.entity.system.GroupAccount;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.model.role.RoleModel;
import com.flowiee.pms.service.system.GroupAccountService;
import com.flowiee.pms.service.system.RoleService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/sys/group-account")
@Tag(name = "Group account API", description = "Quản lý nhóm người dùng")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GroupAccountController extends BaseController {
    RoleService         roleService;
    GroupAccountService groupAccountService;

    @Operation(summary = "Find all group account")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleSystem.readGroupAccount(true)")
    public AppResponse<List<GroupAccount>> findAll(@RequestParam("pageSize") int pageSize, @RequestParam("pageNum") int pageNum) {
        try {
            Page<GroupAccount> groupAccounts = groupAccountService.findAll(pageSize, pageNum - 1);
            return success(groupAccounts.getContent(), pageNum, pageSize, groupAccounts.getTotalPages(), groupAccounts.getTotalElements());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "group account"), ex);
        }
    }

    @Operation(summary = "Find detail group")
    @GetMapping("/{groupId}")
    @PreAuthorize("@vldModuleSystem.readGroupAccount(true)")
    public AppResponse<GroupAccount> findDetailAccount(@PathVariable("groupId") Long groupId) {
        return success(groupAccountService.findById(groupId, true));
    }

    @Operation(summary = "Create group account")
    @PostMapping(value = "/create")
    @PreAuthorize("@vldModuleSystem.insertGroupAccount(true)")
    public AppResponse<GroupAccount> save(@RequestBody GroupAccount groupAccount) {
        try {
            if (groupAccount == null) {
                throw new BadRequestException("Invalid group account");
            }
            return success(groupAccountService.save(groupAccount));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "group account"), ex);
        }
    }

    @Operation(summary = "Update group account")
    @PutMapping(value = "/update/{groupId}")
    @PreAuthorize("@vldModuleSystem.updateGroupAccount(true)")
    public AppResponse<GroupAccount> update(@RequestBody GroupAccount groupAccount, @PathVariable("groupId") Long groupId) {
        return success(groupAccountService.update(groupAccount, groupId));
    }

    @Operation(summary = "Delete group account")
    @DeleteMapping(value = "/delete/{groupId}")
    @PreAuthorize("@vldModuleSystem.deleteGroupAccount(true)")
    public AppResponse<String> delete(@PathVariable("groupId") Long groupId) {
        return success(groupAccountService.delete(groupId));
    }

    @Operation(summary = "Find rights of group")
    @GetMapping("/{groupId}/rights")
    @PreAuthorize("@vldModuleSystem.readGroupAccount(true)")
    public AppResponse<List<RoleModel>> findRights(@PathVariable("groupId") Long groupId) {
        try {
            return success(roleService.findAllRoleByGroupId(groupId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "rights of group account"), ex);
        }
    }

    @Operation(summary = "Grant rights to group")
    @PutMapping(value = "/grant-rights/{groupId}")
    @PreAuthorize("@vldModuleSystem.updateGroupAccount(true)")
    public AppResponse<List<RoleModel>> update(@RequestBody List<RoleModel> rights, @PathVariable("groupId") Long groupId) {
        try {
            if (groupAccountService.findById(groupId, true) == null) {
                throw new BadRequestException("Group not found");
            }
            return success(roleService.updateRightsOfGroup(rights, groupId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "group account"), ex);
        }
    }
}