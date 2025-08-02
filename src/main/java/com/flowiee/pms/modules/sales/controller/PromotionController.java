package com.flowiee.pms.modules.sales.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.modules.sales.entity.PromotionInfo;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.sales.dto.PromotionInfoDTO;
import com.flowiee.pms.modules.sales.model.CreatePromotionReq;
import com.flowiee.pms.modules.sales.service.PromotionService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/promotion")
@Tag(name = "Promotion API", description = "Quản lý promotion, promotion will be deducted from the price of the product")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PromotionController extends BaseController {
    PromotionService mvPromotionService;

    @Operation(summary = "Find all promotions")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleSales.readPromotion(true)")
    public AppResponse<List<PromotionInfoDTO>> findPromotions(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                              @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        Page<PromotionInfoDTO> promotionPage = mvPromotionService.findAll(pageSize, pageNum - 1, null, null, null, null);
        return AppResponse.paged(promotionPage);
    }

    @Operation(summary = "Find detail promotion")
    @GetMapping("/{promotionId}")
    @PreAuthorize("@vldModuleSales.readPromotion(true)")
    public AppResponse<PromotionInfo> findDetailPromotion(@PathVariable("promotionId") Long promotionId) {
        return AppResponse.success(mvPromotionService.findById(promotionId, true));
    }

    @Operation(summary = "Create promotion")
    @PostMapping("/create")
    @PreAuthorize("@vldModuleSales.insertPromotion(true)")
    public AppResponse<PromotionInfoDTO> createPromotion(@RequestBody CreatePromotionReq request) {
        try {
            return AppResponse.success(mvPromotionService.save(request.toPromotionInfoDTO()));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "promotion"), ex);
        }
    }

    @Operation(summary = "Update promotion")
    @PutMapping("/update/{promotionId}")
    @PreAuthorize("@vldModuleSales.updatePromotion(true)")
    public AppResponse<PromotionInfoDTO> updatePromotion(@RequestBody PromotionInfoDTO promotion, @PathVariable("promotionId") Long promotionId) {
        return AppResponse.success(mvPromotionService.update(promotion, promotionId));
    }

    @Operation(summary = "Delete promotion")
    @DeleteMapping("/delete/{promotionId}")
    @PreAuthorize("@vldModuleSales.deletePromotion(true)")
    public AppResponse<String> deletePromotion(@PathVariable("promotionId") Long promotionId) {
        return AppResponse.success(mvPromotionService.delete(promotionId));
    }
}