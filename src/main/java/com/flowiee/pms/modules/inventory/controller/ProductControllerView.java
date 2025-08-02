package com.flowiee.pms.modules.inventory.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.modules.inventory.entity.ProductDescription;
import com.flowiee.pms.modules.inventory.service.*;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.modules.system.model.EximResult;
import com.flowiee.pms.modules.inventory.dto.ProductAttributeDTO;
import com.flowiee.pms.modules.inventory.dto.ProductDTO;
import com.flowiee.pms.common.exception.ResourceNotFoundException;
import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import com.flowiee.pms.modules.system.service.ExportService;

import com.flowiee.pms.common.enumeration.CATEGORY;
import com.flowiee.pms.common.enumeration.Pages;
import com.flowiee.pms.common.enumeration.TemplateExport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/san-pham")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductControllerView extends BaseController {
    ProductInfoService mvProductInfoService;
    ProductImageService mvProductImageService;
    ProductVariantService mvProductVariantService;
    ProductAttributeService mvProductAttributeService;
    @Autowired
    @Qualifier("productExportServiceImpl")
    @NonFinal
    ExportService exportService;

    @GetMapping
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public ModelAndView loadProductPage() {
        setupSearchTool(true, List.of(CATEGORY.SIZE, CATEGORY.COLOR, CATEGORY.PRODUCT_TYPE));
        return baseView(new ModelAndView(Pages.PRO_PRODUCT.getTemplate()));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public ModelAndView viewGeneralProduct(@PathVariable("id") Long productId) {
        ProductDTO product = mvProductInfoService.findById(productId, true);
        ProductDescription lvProductDescription = mvProductInfoService.findDescription(productId);
        if (lvProductDescription != null) {
            product.setDescription(lvProductDescription.getDescription());
        }

        ModelAndView modelAndView = new ModelAndView(Pages.PRO_PRODUCT_INFO.getTemplate());
        modelAndView.addObject("productId", productId);
        modelAndView.addObject("detailProducts", product);
        return baseView(modelAndView);
    }

    @GetMapping(value = "/variant/{id}")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public ModelAndView viewDetailProduct(@PathVariable("id") Long variantId) {
        ProductVariantDTO productVariant = mvProductVariantService.findById(variantId, true);

        ModelAndView modelAndView = new ModelAndView(Pages.PRO_PRODUCT_VARIANT.getTemplate());
        modelAndView.addObject("listAttributes", mvProductAttributeService.findAll(-1, -1, variantId).getContent());
        modelAndView.addObject("bienTheSanPhamId", variantId);
        modelAndView.addObject("bienTheSanPham", productVariant);
        modelAndView.addObject("listImageOfSanPhamBienThe", mvProductImageService.getImageOfProductVariant(variantId));
        //FileStorage imageActive = productVariant.getActiveImage();
        FileStorage imageActive = mvProductImageService.findImageActiveOfProductVariant(variantId);
        if (imageActive == null) {
            imageActive = new FileStorage();
        }
        modelAndView.addObject("imageActive", imageActive);        
        return baseView(modelAndView);
    }

    @PostMapping("/attribute/insert")
    @PreAuthorize("@vldModuleProduct.updateProduct(true)")
    public ModelAndView insertProductAttribute(HttpServletRequest request, @ModelAttribute("thuocTinhSanPham") ProductAttributeDTO productAttribute) {
        mvProductAttributeService.save(productAttribute);
        return refreshPage(request);
    }

    @PostMapping(value = "/attribute/update/{id}")
    @PreAuthorize("@vldModuleProduct.updateProduct(true)")
    public ModelAndView updateProductAttribute(@ModelAttribute("thuocTinhSanPham") ProductAttributeDTO attribute,
                                               @PathVariable("id") Long attributeId,
                                               HttpServletRequest request) {
        if (mvProductAttributeService.findById(attributeId, true) == null) {
            throw new ResourceNotFoundException("Product attribute not found!");
        }
        attribute.setId(attributeId);
        mvProductAttributeService.update(attribute, attributeId);
        return refreshPage(request);
    }

    @DeleteMapping(value = "/attribute/delete/{id}")
    @PreAuthorize("@vldModuleProduct.updateProduct(true)")
    public ResponseEntity<String> deleteAttribute(@PathVariable("id") Long attributeId) {
        return ResponseEntity.ok().body(mvProductAttributeService.delete(attributeId));
    }

    @PostMapping(value = "/variant/active-image/{sanPhamBienTheId}")
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public ModelAndView activeImageOfProductVariant(HttpServletRequest request,
                                                    @PathVariable("sanPhamBienTheId") Long productVariantId,
                                                    @RequestParam("imageId") Long imageId) {
        if (productVariantId == null || productVariantId <= 0 || imageId == null || imageId <= 0) {
            throw new ResourceNotFoundException("Product variant or image not found!");
        }
        mvProductImageService.setImageActiveOfProductVariant(productVariantId, imageId);
        return refreshPage(request);
    }

    @GetMapping("/export")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public ResponseEntity<?> exportData(@RequestParam(value = "isTemplateOnly", required = false) Boolean isTemplateOnly) {
        if (isTemplateOnly == null) {
            isTemplateOnly = false;
        }
        EximResult model = null;
        if (isTemplateOnly) {
            model = exportService.exportToExcel(TemplateExport.IM_LIST_OF_PRODUCTS, null, true);
        } else {
            model = exportService.exportToExcel(TemplateExport.EX_LIST_OF_PRODUCTS, null, false);
        }
        return ResponseEntity.ok().headers(model.getHttpHeaders()).body(model.getContent());
    }

    @GetMapping(value = "/held")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public ModelAndView viewProductHeld() {
        ModelAndView modelAndView = new ModelAndView(Pages.PRO_PRODUCT_HELD.getTemplate());
        modelAndView.addObject("productHeldList", mvProductInfoService.getProductHeldInUnfulfilledOrder());
        return baseView(modelAndView);
    }
}