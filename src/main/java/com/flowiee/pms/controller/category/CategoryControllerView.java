package com.flowiee.pms.controller.category;

import com.flowiee.pms.base.controller.BaseController;
import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.exception.ResourceNotFoundException;
import com.flowiee.pms.model.EximResult;
import com.flowiee.pms.service.ExportService;
import com.flowiee.pms.service.ImportService;
import com.flowiee.pms.service.category.CategoryService;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.enumeration.Pages;

import com.flowiee.pms.common.enumeration.CategoryType;
import com.flowiee.pms.common.enumeration.TemplateExport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryControllerView extends BaseController {
    CategoryService mvCategoryService;
    @Autowired
    @Qualifier("categoryExportServiceImpl")
    @NonFinal
    ExportService mvExportService;
    @Autowired
    @Qualifier("categoryImportServiceImpl")
    @NonFinal
    ImportService mvImportService;

    @GetMapping
    @PreAuthorize("@vldModuleCategory.readCategory(true)")
    public ModelAndView viewRootCategory() {
        ModelAndView modelAndView = new ModelAndView(Pages.CTG_CATEGORY.getTemplate());
        modelAndView.addObject("category", new Category());
        modelAndView.addObject("listCategory", mvCategoryService.findRootCategory());
        return baseView(modelAndView);
    }

    @GetMapping("/{type}")
    @PreAuthorize("@vldModuleCategory.readCategory(true)")
    public ModelAndView viewSubCategory(@PathVariable("type") String categoryType) {
        if (CommonUtils.getCategoryType(categoryType) == null) {
            throw new ResourceNotFoundException("Category not found!");
        }
        ModelAndView modelAndView = new ModelAndView(Pages.CTG_CATEGORY_DETAIL.getTemplate());
        modelAndView.addObject("categoryType", categoryType);
        modelAndView.addObject("ctgRootName", CategoryType.valueOf(CommonUtils.getCategoryType(categoryType)).getLabel());
        modelAndView.addObject("templateImportName", TemplateExport.EX_LIST_OF_CATEGORIES);
        modelAndView.addObject("url_template", "");
        modelAndView.addObject("url_import", "");
        modelAndView.addObject("url_export", "");
        return baseView(modelAndView);
    }

    @GetMapping("/{type}/template")
    @PreAuthorize("@vldModuleCategory.importCategory(true)")
    public ResponseEntity<InputStreamResource> exportTemplate(@PathVariable("type") String categoryType) {
        EximResult model = mvExportService.exportToExcel(TemplateExport.EX_LIST_OF_CATEGORIES, null, true);
        return ResponseEntity.ok().headers(model.getHttpHeaders()).body(model.getContent());
    }

    @PostMapping("/{type}/import")
    @PreAuthorize("@vldModuleCategory.importCategory(true)")
    public ModelAndView importData(@PathVariable("type") String categoryType, @RequestParam("file") MultipartFile file) {
        if (CommonUtils.getCategoryType(categoryType) == null) {
            throw new ResourceNotFoundException("Category not found!");
        }
        mvImportService.importFromExcel(TemplateExport.IM_LIST_OF_CATEGORIES, file);
        return new ModelAndView("redirect:/{type}");
    }

    @GetMapping("/{type}/export")
    @PreAuthorize("@vldModuleCategory.readCategory(true)")
    public ResponseEntity<InputStreamResource> exportData(@PathVariable("type") String categoryType) {
        EximResult model = mvExportService.exportToExcel(TemplateExport.EX_LIST_OF_CATEGORIES, null, false);
        return ResponseEntity.ok().headers(model.getHttpHeaders()).body(model.getContent());
    }
}