package com.flowiee.pms.controller.sales;

import com.flowiee.pms.base.BaseController;
import com.flowiee.pms.entity.sales.PromotionInfo;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.model.dto.PromotionInfoDTO;
import com.flowiee.pms.model.payload.CreatePromotionReq;
import com.flowiee.pms.service.sales.PromotionService;
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
        return success(promotionPage.getContent(), pageNum, pageSize, promotionPage.getTotalPages(), promotionPage.getTotalElements());
    }

    @Operation(summary = "Find detail promotion")
    @GetMapping("/{promotionId}")
    @PreAuthorize("@vldModuleSales.readPromotion(true)")
    public AppResponse<PromotionInfo> findDetailPromotion(@PathVariable("promotionId") Long promotionId) {
        return success(mvPromotionService.findById(promotionId, true));
    }

    @Operation(summary = "Create promotion")
    @PostMapping("/create")
    @PreAuthorize("@vldModuleSales.insertPromotion(true)")
    public AppResponse<PromotionInfoDTO> createPromotion(@RequestBody CreatePromotionReq request) {
        try {
            return success(mvPromotionService.save(request.toPromotionInfoDTO()));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "promotion"), ex);
        }
    }

    @Operation(summary = "Update promotion")
    @PutMapping("/update/{promotionId}")
    @PreAuthorize("@vldModuleSales.updatePromotion(true)")
    public AppResponse<PromotionInfoDTO> updatePromotion(@RequestBody PromotionInfoDTO promotion, @PathVariable("promotionId") Long promotionId) {
        return success(mvPromotionService.update(promotion, promotionId));
    }

    @Operation(summary = "Delete promotion")
    @DeleteMapping("/delete/{promotionId}")
    @PreAuthorize("@vldModuleSales.deletePromotion(true)")
    public AppResponse<String> deletePromotion(@PathVariable("promotionId") Long promotionId) {
        return success(mvPromotionService.delete(promotionId));
    }
}