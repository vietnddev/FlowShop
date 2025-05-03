package com.flowiee.pms.modules.product.controller;

import com.flowiee.pms.common.enumeration.Pages;
import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.exception.ResourceNotFoundException;
import com.flowiee.pms.modules.product.dto.MaterialDTO;
import com.flowiee.pms.modules.product.service.MaterialService;

import com.flowiee.pms.common.enumeration.EndPoint;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/stg/material")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MaterialControllerView extends BaseController {
    MaterialService mvMaterialService;

    @GetMapping
    @PreAuthorize("@vldModuleProduct.readMaterial(true)")
    public ModelAndView viewMaterials() {
        ModelAndView modelAndView = new ModelAndView(Pages.STG_MATERIAL.getTemplate());
        modelAndView.addObject("templateImportName", "Name");
        modelAndView.addObject("url_template", EndPoint.URL_STG_MATERIAL_IMPORT_TEMPLATE.getValue());
        modelAndView.addObject("url_import", EndPoint.URL_STG_MATERIAL_IMPORT.getValue());
        modelAndView.addObject("url_export", EndPoint.URL_STG_MATERIAL_EXPORT.getValue());
        return baseView(modelAndView);
    }

    @PostMapping("/insert")
    @PreAuthorize("@vldModuleProduct.insertMaterial(true)")
    public ModelAndView insert(@ModelAttribute("material") MaterialDTO material) {
        material.setStatus(true);
        mvMaterialService.save(material);
        return new ModelAndView("redirect:");
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("@vldModuleProduct.updateMaterial(true)")
    public ModelAndView update(@ModelAttribute("material") MaterialDTO material,
                                               @PathVariable("id") Long materialId,
                                               HttpServletRequest request) {
        if (mvMaterialService.findById(materialId, true) == null) {
            throw new ResourceNotFoundException("Material not found!");
        }
        mvMaterialService.update(material, materialId);
        return refreshPage(request);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("@vldModuleProduct.deleteMaterial(true)")
    public ModelAndView delete(@PathVariable("id") Long materialId, HttpServletRequest request) {
        mvMaterialService.delete(materialId);
        return refreshPage(request);
    }

    @GetMapping("/template")
    @PreAuthorize("@vldModuleProduct.readMaterial(true)")
    public ResponseEntity<?> exportTemplate() {
        return null;
    }

    @PostMapping("/import")
    @PreAuthorize("@vldModuleProduct.insertMaterial(true)")
    public ModelAndView importData(@RequestParam("file") MultipartFile file) {
//        if (kiemTraQuyenModule.kiemTraQuyenExport()) {
//            donViTinhService.importData(file);
//            return "redirect:" + EndPointUtil.DANHMUC_DONVITINH_VIEW;
//        } else {
//            return PagesUtil.PAGE_UNAUTHORIZED;
//        }
        return null;
    }

    @GetMapping("/export")
    @PreAuthorize("@vldModuleProduct.readMaterial(true)")
    public ResponseEntity<?> exportData() {
//        if (kiemTraQuyenModule.kiemTraQuyenExport()) {
//            byte[] dataExport = donViTinhService.exportData();
//            HttpHeaders header = new HttpHeaders();
//            header.setContentType(new MediaType("application", "force-download"));
//            header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + FileUtil.TEMPLATE_IE_DM_LOAIDONVITINH + ".xlsx");
//            return new ResponseEntity<>(new ByteArrayResource(dataExport), header, HttpStatus.CREATED);
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(PagesUtil.PAGE_UNAUTHORIZED);
//        }
        return null;
    }
}