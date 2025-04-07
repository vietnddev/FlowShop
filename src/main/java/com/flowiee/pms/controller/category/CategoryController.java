package com.flowiee.pms.controller.category;

import com.flowiee.pms.base.BaseController;
import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.exception.ResourceNotFoundException;
import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.model.dto.CategoryDTO;
import com.flowiee.pms.service.category.CategoryService;
import com.flowiee.pms.common.utils.CommonUtils;
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
import java.util.Objects;

@RestController
@RequestMapping("${app.api.prefix}/category")
@Tag(name = "Category API", description = "Quản lý danh mục hệ thống")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryController extends BaseController {
    CategoryService mvCategoryService;

    @Operation(summary = "Find all category")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleCategory.readCategory(true)")
    public AppResponse<List<Category>> findAll() {
        return success(mvCategoryService.findRootCategory());
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
            return success(categories.getContent(), pageNum, pageSize, categories.getTotalPages(), categories.getTotalElements());
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
            return success(mvCategoryService.save(category));
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
            return success(mvCategoryService.update(category, categoryId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "category"), ex);
        }
    }

    @Operation(summary = "Delete category")
    @DeleteMapping("/delete/{categoryId}")
    @PreAuthorize("@vldModuleCategory.deleteCategory(true)")
    public AppResponse<String> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        return success(mvCategoryService.delete(categoryId));
    }
}