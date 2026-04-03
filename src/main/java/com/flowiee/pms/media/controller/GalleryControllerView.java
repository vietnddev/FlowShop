package com.flowiee.pms.media.controller;

import com.flowiee.pms.product.service.ProductService;
import com.flowiee.pms.shared.base.BaseController;
import com.flowiee.pms.media.dto.FileDTO;
import com.flowiee.pms.product.service.ProductImageService;
import com.flowiee.pms.shared.enums.Pages;
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
    ProductService mvProductService;
    ProductImageService mvProductImageService;

    @GetMapping
    @PreAuthorize("@vldModuleProduct.readGallery(true)")
    public ModelAndView viewGallery() {
        ModelAndView modelAndView = new ModelAndView(Pages.PRO_GALLERY.getTemplate());
        modelAndView.addObject("listImages", FileDTO.toDTOs(mvProductImageService.getImageOfProduct(null)));
        modelAndView.addObject("listProducts", mvProductService.findProductsIdAndProductName());
        return baseView(modelAndView);
    }
}