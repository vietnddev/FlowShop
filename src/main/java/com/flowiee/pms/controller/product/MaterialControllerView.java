package com.flowiee.pms.controller.product;

import com.flowiee.pms.entity.product.Material;
import com.flowiee.pms.utils.PagesUtils;
import com.flowiee.pms.controller.BaseController;
import com.flowiee.pms.exception.NotFoundException;
import com.flowiee.pms.service.product.MaterialService;

import com.flowiee.pms.utils.constants.EndPoint;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/stg/material")
public class MaterialControllerView extends BaseController {
    private final MaterialService materialService;

    public MaterialControllerView(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    @PreAuthorize("@vldModuleProduct.readMaterial(true)")
    public ModelAndView viewMaterials() {
        ModelAndView modelAndView = new ModelAndView(PagesUtils.STG_MATERIAL);
        modelAndView.addObject("templateImportName", "Name");
        modelAndView.addObject("url_template", EndPoint.URL_STG_MATERIAL_IMPORT_TEMPLATE.getValue());
        modelAndView.addObject("url_import", EndPoint.URL_STG_MATERIAL_IMPORT.getValue());
        modelAndView.addObject("url_export", EndPoint.URL_STG_MATERIAL_EXPORT.getValue());
        return baseView(modelAndView);
    }

    @PostMapping("/insert")
    @PreAuthorize("@vldModuleProduct.insertMaterial(true)")
    public ModelAndView insert(@ModelAttribute("material") Material material) {
        material.setStatus(true);
        materialService.save(material);
        return new ModelAndView("redirect:");
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("@vldModuleProduct.updateMaterial(true)")
    public ModelAndView update(@ModelAttribute("material") Material material,
                                               @PathVariable("id") Integer materialId,
                                               HttpServletRequest request) {
        if (materialId <= 0 || materialService.findById(materialId).isEmpty()) {
            throw new NotFoundException("Material not found!");
        }
        materialService.update(material, materialId);
        return new ModelAndView("redirect:" + request.getHeader("referer"));
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("@vldModuleProduct.deleteMaterial(true)")
    public ModelAndView delete(@PathVariable("id") Integer materialId, HttpServletRequest request) {
        materialService.delete(materialId);
        return new ModelAndView("redirect:" + request.getHeader("referer"));
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