package com.flowiee.pms.system.controller;

import com.flowiee.pms.shared.base.BaseController;
import com.flowiee.pms.system.entity.Category;
import com.flowiee.pms.shared.exception.AppException;
import com.flowiee.pms.shared.exception.ResourceNotFoundException;
import com.flowiee.pms.shared.response.AppResponse;
import com.flowiee.pms.system.dto.CategoryDTO;
import com.flowiee.pms.system.service.CategoryService;
import com.flowiee.pms.shared.util.CommonUtils;
import com.flowiee.pms.shared.enums.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("${app.api.prefix}/category")
@Tag(name = "Category API", description = "Quản lý danh mục hệ thống")
@RequiredArgsConstructor
public class CategoryController extends BaseController {
    private final CategoryService mvCategoryService;

    @Operation(summary = "Find all category")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleCategory.readCategory(true)")
    public AppResponse<List<Category>> findAll() {
        return AppResponse.success(mvCategoryService.findRootCategory());
    }

    @Operation(summary = "Find by type")
    @GetMapping("/{type}")
    @PreAuthorize("@vldModuleCategory.readCategory(true)")
    public AppResponse<List<Category>> findByType(@PathVariable("type") String categoryType,
                                                  @RequestParam(name = "parentId", required = false) Long parentId,
                                                  @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                                  @RequestParam(name = "pageNum", required = false) Integer pageNum) {
        try {
            if (Objects.isNull(pageSize) || Objects.isNull(pageNum)) {
                pageSize = -1;
                pageNum = -1;
            }
            Page<Category> categories = mvCategoryService.findSubCategory(CommonUtils.getCategoryEnum(categoryType), parentId, null, pageSize, pageNum - 1);
            return AppResponse.paged(categories);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "category"), ex);
        }
    }

    @Operation(summary = "Create category")
    @PostMapping("/create")
    @PreAuthorize("@vldModuleCategory.insertCategory(true)")
    public AppResponse<CategoryDTO> createCategory(@RequestBody CategoryDTO category) {
        try {
            category.setType(CommonUtils.getCategoryType(category.getType()));
            return AppResponse.success(mvCategoryService.create(category));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "category"), ex);
        }
    }

    @Operation(summary = "Update category")
    @PutMapping("/update/{categoryId}")
    @PreAuthorize("@vldModuleCategory.updateCategory(true)")
    public AppResponse<CategoryDTO> updateCategory(@RequestBody CategoryDTO category, @PathVariable("categoryId") Long categoryId) {
        if (mvCategoryService.findById(categoryId, true) == null) {
            throw new ResourceNotFoundException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "category"));
        }
        try {
            category.setType(CommonUtils.getCategoryType(category.getType()));
            return AppResponse.success(mvCategoryService.update(category, categoryId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "category"), ex);
        }
    }

    @Operation(summary = "Delete category")
    @DeleteMapping("/delete/{categoryId}")
    @PreAuthorize("@vldModuleCategory.deleteCategory(true)")
    public AppResponse<String> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        return AppResponse.success("Success: " + mvCategoryService.delete(categoryId));
    }
}