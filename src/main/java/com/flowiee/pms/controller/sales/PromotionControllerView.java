package com.flowiee.pms.controller.sales;

import com.flowiee.pms.base.BaseController;
import com.flowiee.pms.model.dto.PromotionInfoDTO;
import com.flowiee.pms.service.sales.PromotionService;
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
@RequestMapping("/promotion")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PromotionControllerView extends BaseController {
    PromotionService mvPromotionService;

    @GetMapping
    @PreAuthorize("@vldModuleSales.readPromotion(true)")
    public ModelAndView findAll() {
        return baseView(new ModelAndView(Pages.PRO_PROMOTION.getTemplate()));
    }

    @GetMapping(value = "/{promotionId}")
    @PreAuthorize("@vldModuleSales.readPromotion(true)")
    public ModelAndView findDetail(@PathVariable("promotionId") Long promotionId) {
        PromotionInfoDTO promotion = mvPromotionService.findById(promotionId, true);

        ModelAndView modelAndView = new ModelAndView(Pages.PRO_PROMOTION_DETAIL.getTemplate());
        modelAndView.addObject("promotion", promotion);
        return baseView(modelAndView);
    }
}