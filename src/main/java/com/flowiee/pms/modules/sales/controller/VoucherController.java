package com.flowiee.pms.modules.sales.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.sales.dto.VoucherInfoDTO;
import com.flowiee.pms.modules.sales.entity.VoucherInfo;
import com.flowiee.pms.modules.sales.entity.VoucherTicket;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.sales.dto.VoucherTicketDTO;
import com.flowiee.pms.modules.sales.model.CreateVoucherReq;
import com.flowiee.pms.modules.sales.service.VoucherService;
import com.flowiee.pms.modules.sales.service.VoucherTicketService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/voucher")
@Tag(name = "Voucher API", description = "Quản lý voucher, voucher will be deducted from the value of the order")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VoucherController extends BaseController {
    VoucherService       mvVoucherService;
    VoucherTicketService mvVoucherTicketService;

    @Operation(summary = "Find all voucher")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleSales.readVoucher(true)")
    public AppResponse<List<VoucherInfoDTO>> findAll(@RequestParam("pageSize") int pageSize,
                                                     @RequestParam("pageNum") int pageNum,
                                                     @RequestParam(value = "title", required = false) String pTitle,
                                                     @RequestParam(value = "startTime", required = false)
                                                         @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDateTime pStartTime,
                                                     @RequestParam(value = "endTime", required = false)
                                                         @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDateTime pEndTime,
                                                     @RequestParam(value = "status", required = false) String pStatus) {
        try {
            Page<VoucherInfoDTO> voucherInfos = mvVoucherService.findAll(pageSize, pageNum - 1, null, pTitle, pStartTime, pEndTime, pStatus);
            return AppResponse.success(voucherInfos.getContent(), pageNum, pageSize, voucherInfos.getTotalPages(), voucherInfos.getTotalElements());
        } catch (Exception ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "voucher"), ex);
        }
    }

    @Operation(summary = "Find detail voucher")
    @GetMapping("/{voucherInfoId}")
    @PreAuthorize("@vldModuleSales.readVoucher(true)")
    public AppResponse<VoucherInfoDTO> findDetailVoucherInfo(@PathVariable("voucherInfoId") Long voucherInfoId) {
        return AppResponse.success(mvVoucherService.findById(voucherInfoId, true));
    }

    @Operation(summary = "Create voucher")
    @PostMapping("/create")
    @PreAuthorize("@vldModuleSales.insertVoucher(true)")
    public AppResponse<VoucherInfo> createVoucher(@RequestBody CreateVoucherReq request) {
        if (ObjectUtils.isEmpty(request.getApplicableProducts())) {
            throw new BadRequestException("Sản phẩm được áp dụng không được rỗng!");
        }
        return AppResponse.success(mvVoucherService.save(request.toPromotionInfoDTO()));
    }

    @Operation(summary = "Update voucher")
    @PutMapping("/update/{voucherInfoId}")
    @PreAuthorize("@vldModuleSales.updateVoucher(true)")
    public AppResponse<VoucherInfoDTO> updateVoucher(@RequestBody VoucherInfoDTO voucherInfo, @PathVariable("voucherInfoId") Long voucherInfoId) {
        return AppResponse.success(mvVoucherService.update(voucherInfo ,voucherInfoId));
    }

    @Operation(summary = "Delete voucher")
    @DeleteMapping("/delete/{voucherInfoId}")
    @PreAuthorize("@vldModuleSales.deleteVoucher(true)")
    public AppResponse<String> deleteVoucher(@PathVariable("voucherInfoId") Long voucherInfoId) {
        return AppResponse.success(mvVoucherService.delete(voucherInfoId));
    }

    @Operation(summary = "Check the voucher is available")
    @GetMapping("/check/{voucherCode}")
    @PreAuthorize("@vldModuleSales.readVoucher(true)")
    public AppResponse<VoucherTicketDTO> isAvailableVoucher(@PathVariable("voucherCode") String voucherCode) {
        try {
            VoucherTicketDTO voucherTicket = mvVoucherTicketService.isAvailable(voucherCode);
            if (voucherTicket == null) {
                return AppResponse.success(new VoucherTicketDTO("N"));
            }
            return AppResponse.success(mvVoucherTicketService.isAvailable(voucherCode));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "voucher"), ex);
        }
    }

    @Operation(summary = "Get tickets of voucher by voucherId")
    @GetMapping("/{voucherInfoId}/tickets")
    @PreAuthorize("@vldModuleSales.readVoucher(true)")
    public AppResponse<List<VoucherTicket>> getTicketsByVoucherInfo(@PathVariable("voucherInfoId") Long voucherInfoId,
                                                                    @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                    @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        try {
            if (pageSize != null && pageNum != null) {
                Page<VoucherTicket> voucherTickets = mvVoucherTicketService.findAll(pageSize, pageNum - 1, voucherInfoId);
                return AppResponse.success(voucherTickets.getContent(), pageNum, pageSize, voucherTickets.getTotalPages(), voucherTickets.getTotalElements());
            }
            return AppResponse.success(mvVoucherTicketService.findByVoucherId(voucherInfoId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "voucher ticket"), ex);
        }
    }
}