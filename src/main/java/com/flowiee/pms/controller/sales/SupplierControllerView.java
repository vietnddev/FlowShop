package com.flowiee.pms.controller.sales;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.flowiee.pms.base.BaseController;
import com.flowiee.pms.common.enumeration.Pages;
import com.flowiee.pms.service.sales.SupplierService;

@Controller
@RequestMapping("/san-pham/supplier")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SupplierControllerView extends BaseController {
	SupplierService mvSupplierService;

	@GetMapping
	@PreAuthorize("@vldModuleSales.readSupplier(true)")
	public ModelAndView viewAllSupplier() {
		ModelAndView modelAndView = new ModelAndView(Pages.PRO_SUPPLIER.getTemplate());
		modelAndView.addObject("listSupplier", mvSupplierService.findAll(-1, -1, null).getContent());
		return baseView(modelAndView);
	}
}