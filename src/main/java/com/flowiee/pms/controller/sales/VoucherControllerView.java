package com.flowiee.pms.controller.sales;

import com.flowiee.pms.base.BaseController;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.ResourceNotFoundException;
import com.flowiee.pms.model.dto.VoucherInfoDTO;
import com.flowiee.pms.service.sales.VoucherService;
import com.flowiee.pms.common.enumeration.Pages;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/san-pham/voucher")
@Tag(name = "Voucher API", description = "Quản lý voucher")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VoucherControllerView extends BaseController {
    VoucherService mvVoucherService;

    @GetMapping
    @PreAuthorize("@vldModuleSales.readVoucher(true)")
    public ModelAndView viewVouchers() {
        return baseView(new ModelAndView(Pages.PRO_VOUCHER.getTemplate()));
    }

    @GetMapping("/detail/{id}")
    @PreAuthorize("@vldModuleSales.readVoucher(true)")
    public ModelAndView viewVoucherDetail(@PathVariable("id") Integer voucherInfoId) {
        ModelAndView modelAndView = new ModelAndView(Pages.PRO_VOUCHER_DETAIL.getTemplate());
        modelAndView.addObject("voucherInfoId", voucherInfoId);
        return baseView(modelAndView);
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("@vldModuleSales.updateVoucher(true)")
    public ModelAndView deleteVoucher(@ModelAttribute("voucherInfo") VoucherInfoDTO voucherInfo, @PathVariable("id") Long voucherInfoId) {
        if (voucherInfo == null) {
            throw new BadRequestException("Voucher to update not null!");
        }
        if (voucherInfoId <= 0 || mvVoucherService.findById(voucherInfoId, true) == null) {
            throw new ResourceNotFoundException("VoucherId invalid!");
        }
        mvVoucherService.update(voucherInfo ,voucherInfoId);
        return new ModelAndView("redirect:/san-pham/voucher");
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("@vldModuleSales.deleteVoucher(true)")
    public ModelAndView deleteVoucher(@PathVariable("id") Long voucherInfoId) {
        mvVoucherService.delete(voucherInfoId);
        return new ModelAndView("redirect:/san-pham/voucher");
    }
}