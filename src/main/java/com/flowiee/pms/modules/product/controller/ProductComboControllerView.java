package com.flowiee.pms.modules.product.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.modules.product.dto.ProductComboDTO;
import com.flowiee.pms.modules.product.service.ProductComboService;
import com.flowiee.pms.common.enumeration.Pages;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/product/combo")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductComboControllerView extends BaseController {
    ProductComboService mvProductComboService;

    @GetMapping
    @PreAuthorize("@vldModuleProduct.readCombo(true)")
    public ModelAndView findAll() {
        return baseView(new ModelAndView(Pages.PRO_COMBO.getTemplate()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@vldModuleProduct.readCombo(true)")
    public ModelAndView findDetail(@PathVariable("id") Long productComboId) {
        ProductComboDTO productCombo = mvProductComboService.findById(productComboId, true);

        ModelAndView modelAndView = new ModelAndView(Pages.PRO_COMBO_DETAIL.getTemplate());
        modelAndView.addObject("productComboDetail", productCombo);
        return baseView(modelAndView);
    }
}