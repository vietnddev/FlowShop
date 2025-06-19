package com.flowiee.pms.modules.inventory.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.modules.inventory.service.ProductDamagedService;
import com.flowiee.pms.common.enumeration.Pages;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/san-pham")
@RequiredArgsConstructor
public class ProductDamagedControllerView extends BaseController {
    private final ProductDamagedService productDamagedService;

    @GetMapping("/damaged")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public ModelAndView loadProductPage() {
        setupSearchTool(true, List.of());
        ModelAndView modelAndView = new ModelAndView(Pages.PRO_PRODUCT_DAMAGED.getTemplate());
        modelAndView.addObject("productDamagedList", productDamagedService.findAll());
        return baseView(modelAndView);
    }
}