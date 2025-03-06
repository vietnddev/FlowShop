package com.flowiee.pms.controller.sales;

import com.flowiee.pms.base.BaseController;
import com.flowiee.pms.entity.product.GiftCatalog;
import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.service.product.GiftCatalogService;
import com.flowiee.pms.service.sales.GiftRedemptionService;
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
    public AppResponse<List<GiftCatalog>> getActiveGifts() {
        return success(giftCatalogService.getActiveGifts());
    }

    @Operation(summary = "Redeem gift")
    @PostMapping("/redeem")
    public AppResponse<String> redeemGift(@RequestParam Long customerId, @RequestParam Long giftId) {
        redemptionService.redeemGift(customerId, giftId);
        return success("Đổi quà thành công!");
    }
}