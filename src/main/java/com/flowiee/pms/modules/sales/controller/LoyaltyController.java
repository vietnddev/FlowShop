package com.flowiee.pms.modules.sales.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.inventory.dto.GiftCatalogDTO;
import com.flowiee.pms.modules.inventory.service.GiftCatalogService;
import com.flowiee.pms.modules.inventory.service.GiftRedemptionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/loyalty")
@RequiredArgsConstructor
public class LoyaltyController extends BaseController {
    private final GiftCatalogService giftCatalogService;
    private final GiftRedemptionService redemptionService;

    @Operation(summary = "Get list of gifts")
    @GetMapping("/gifts")
    public AppResponse<List<GiftCatalogDTO>> getActiveGifts() {
        return AppResponse.success(giftCatalogService.getActiveGifts());
    }

    @Operation(summary = "Redeem gift")
    @PostMapping("/redeem")
    public AppResponse<String> redeemGift(@RequestParam Long customerId, @RequestParam Long giftId) {
        redemptionService.redeemGift(customerId, giftId);
        return AppResponse.success("Đổi quà thành công!");
    }
}