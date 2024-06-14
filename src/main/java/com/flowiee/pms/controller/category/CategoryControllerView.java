package com.flowiee.pms.controller.category;

import com.flowiee.pms.controller.BaseController;
import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.exception.NotFoundException;
import com.flowiee.pms.model.ExportDataModel;
import com.flowiee.pms.service.ExportService;
import com.flowiee.pms.service.category.CategoryService;
import com.flowiee.pms.utils.CommonUtils;
import com.flowiee.pms.utils.PagesUtils;

import com.flowiee.pms.utils.constants.CategoryType;
import com.flowiee.pms.utils.constants.TemplateExport;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@CrossOrigin
@RestController
@RequestMapping("/system/category")
public class CategoryControllerView extends BaseController {
    private final CategoryService categoryService;
    private final ExportService   exportService;

    public CategoryControllerView(CategoryService categoryService, @Qualifier("categoryExportServiceImpl") ExportService exportService) {
        this.categoryService = categoryService;
        this.exportService = exportService;
    }

    @GetMapping
    @PreAuthorize("@vldModuleCategory.readCategory(true)")
    public ModelAndView viewRootCategory() {
        ModelAndView modelAndView = new ModelAndView(PagesUtils.CTG_CATEGORY);
        modelAndView.addObject("category", new Category());
        modelAndView.addObject("listCategory", categoryService.findRootCategory());
        return baseView(modelAndView);
    }

    @GetMapping("/{type}")
    @PreAuthorize("@vldModuleCategory.readCategory(true)")
    public ModelAndView viewSubCategory(@PathVariable("type") String categoryType) {
        if (CommonUtils.getCategoryType(categoryType) == null) {
            throw new NotFoundException("Category not found!");
        }
        ModelAndView modelAndView = new ModelAndView(PagesUtils.CTG_CATEGORY_DETAIL);
        modelAndView.addObject("categoryType", categoryType);
        modelAndView.addObject("ctgRootName", CategoryType.valueOf(CommonUtils.getCategoryType(categoryType)).getLabel());
        modelAndView.addObject("templateImportName", TemplateExport.LIST_OF_CATEGORIES);
        modelAndView.addObject("url_template", "");
        modelAndView.addObject("url_import", "");
        modelAndView.addObject("url_export", "");
        return baseView(modelAndView);
    }

    @GetMapping("/{type}/template")
    @PreAuthorize("@vldModuleCategory.importCategory(true)")
    public ResponseEntity<InputStreamResource> exportTemplate(@PathVariable("type") String categoryType) {
        ExportDataModel model = exportService.exportToExcel(TemplateExport.LIST_OF_CATEGORIES, null, true);
        return ResponseEntity.ok().headers(model.getHttpHeaders()).body(model.getContent());
    }

    @PostMapping("/{type}/import")
    @PreAuthorize("@vldModuleCategory.importCategory(true)")
    public ModelAndView importData(@PathVariable("type") String categoryType, @RequestParam("file") MultipartFile file) {
        if (CommonUtils.getCategoryType(categoryType) == null) {
            throw new NotFoundException("Category not found!");
        }
        categoryService.importData(file, categoryType);
        return new ModelAndView("redirect:/{type}");
    }

    @GetMapping("/{type}/export")
    @PreAuthorize("@vldModuleCategory.readCategory(true)")
    public ResponseEntity<InputStreamResource> exportData(@PathVariable("type") String categoryType) {
        ExportDataModel model = exportService.exportToExcel(TemplateExport.LIST_OF_CATEGORIES, null, false);
        return ResponseEntity.ok().headers(model.getHttpHeaders()).body(model.getContent());
    }
}