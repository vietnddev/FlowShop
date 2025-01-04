package com.flowiee.pms.controller.product;

import com.flowiee.pms.base.controller.BaseController;
import com.flowiee.pms.model.dto.FileDTO;
import com.flowiee.pms.service.product.ProductImageService;
import com.flowiee.pms.service.product.ProductInfoService;
import com.flowiee.pms.common.enumeration.Pages;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/gallery")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GalleryControllerView extends BaseController {
    ProductInfoService  mvProductInfoService;
    ProductImageService mvProductImageService;

    @GetMapping
    @PreAuthorize("@vldModuleProduct.readGallery(true)")
    public ModelAndView viewGallery() {
        ModelAndView modelAndView = new ModelAndView(Pages.PRO_GALLERY.getTemplate());
        modelAndView.addObject("listImages", FileDTO.fromFileStorages(mvProductImageService.getImageOfProduct(null)));
        modelAndView.addObject("listProducts", mvProductInfoService.findProductsIdAndProductName());
        return baseView(modelAndView);
    }
}