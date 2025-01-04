package com.flowiee.pms.controller.system;

import com.flowiee.pms.base.controller.BaseController;
import com.flowiee.pms.entity.system.Branch;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.service.system.BranchService;
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

    @Operation(summary = "Find all branches")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleSystem.readBranch(true)")
    public AppResponse<List<Branch>> findAllBranches() {
        try {
            return success(branchService.findAll());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "branch"), ex);
        }
    }

    @PostMapping
    @PreAuthorize("@vldModuleSystem.insertBranch(true)")
    public AppResponse<Branch> createBranch(@RequestBody Branch branch) {
        try {
            return success(branchService.save(branch));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.DELETE_ERROR_OCCURRED.getDescription(), "branch"), ex);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("@vldModuleSystem.updateBranch(true)")
    public AppResponse<Branch> updateBranch(@RequestBody Branch branch, @PathVariable("id") Long branchId) {
        try {
            return success(branchService.update(branch, branchId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.DELETE_ERROR_OCCURRED.getDescription(), "branch"), ex);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("@vldModuleSystem.deleteBranch(true)")
    public AppResponse<String> deleteBranch(@PathVariable("id") Long branchId) {
        try {
            return success(branchService.delete(branchId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.DELETE_ERROR_OCCURRED.getDescription(), "branch"), ex);
        }
    }
}