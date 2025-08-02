package com.flowiee.pms.modules.system.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.base.controller.ControllerHelper;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.modules.system.dto.BranchDTO;
import com.flowiee.pms.modules.system.service.BranchService;
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
@RequestMapping("${app.api.prefix}/system/branch")
@Tag(name = "Branch API", description = "Quản lý danh sách chi nhánh")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BranchController extends BaseController {
    BranchService branchService;
    ControllerHelper mvCHelper;

    @Operation(summary = "Find all branches")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleSystem.readBranch(true)")
    public AppResponse<List<BranchDTO>> findAllBranches() {
        try {
            return mvCHelper.success(branchService.find());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "branch"), ex);
        }
    }

    @PostMapping
    @PreAuthorize("@vldModuleSystem.insertBranch(true)")
    public AppResponse<BranchDTO> createBranch(@RequestBody BranchDTO branch) {
        try {
            return mvCHelper.success(branchService.save(branch));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.DELETE_ERROR_OCCURRED.getDescription(), "branch"), ex);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("@vldModuleSystem.updateBranch(true)")
    public AppResponse<BranchDTO> updateBranch(@RequestBody BranchDTO branch, @PathVariable("id") Long branchId) {
        try {
            return mvCHelper.success(branchService.update(branch, branchId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.DELETE_ERROR_OCCURRED.getDescription(), "branch"), ex);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("@vldModuleSystem.deleteBranch(true)")
    public AppResponse<String> deleteBranch(@PathVariable("id") Long branchId) {
        try {
            return mvCHelper.success(branchService.delete(branchId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.DELETE_ERROR_OCCURRED.getDescription(), "branch"), ex);
        }
    }
}