package com.flowiee.pms.modules.sales.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.base.controller.ControllerHelper;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.sales.dto.ItemsDTO;
import com.flowiee.pms.modules.sales.dto.OrderCartDTO;
import com.flowiee.pms.modules.sales.model.CartItemsReq;
import com.flowiee.pms.modules.sales.model.CartReq;
import com.flowiee.pms.modules.sales.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/sls/cart")
@Tag(name = "Cart API", description = "Quản lý giỏ hàng")
@RequiredArgsConstructor
public class CartController extends BaseController {
    private final CartService mvCartService;
    private final ControllerHelper mvCHelper;

    @GetMapping
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public AppResponse<List<OrderCartDTO>> getUserCarts() {
        return mvCHelper.success(mvCartService.findCurrentUserCarts());
    }

    @GetMapping("/{cartId}")
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public AppResponse<OrderCartDTO> getItems(@PathVariable("cartId") Long pCartId) {
        return mvCHelper.success(mvCartService.findDtoById(pCartId, true));
    }

    @PostMapping("/add-items")
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public AppResponse<String> addItemsToCart(@RequestBody CartReq request) {
        mvCartService.addItemsToCart(request);
        return mvCHelper.success(null, "Success!");
    }

    @PostMapping("/add")
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public AppResponse<OrderCartDTO> addDraftCart() {
        return mvCHelper.success(mvCartService.addDraftCart(), "Success");
    }

    @DeleteMapping("/{cartId}")
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public AppResponse<String> deleteCart(@PathVariable("cartId") Long cartId) {
        return mvCHelper.success(mvCartService.delete(cartId));
    }

    @DeleteMapping("/{cartId}/item/{itemId}")
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public AppResponse<String> deleteItem(@PathVariable("cartId") Long cartId, @PathVariable("itemId") Long itemId) {
        return mvCHelper.success(mvCartService.deleteItem(cartId, itemId));
    }

    @PutMapping("/{cartId}/item/{itemId}/update-quantity")
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public AppResponse<ItemsDTO> updateItemQuantity(@PathVariable("cartId") Long cartId,
                                            @PathVariable("itemId") Long itemId,
                                            @RequestBody CartItemsReq req) {
        return mvCHelper.success(mvCartService.updateItemQuantity(cartId, itemId, req.getQuantity()));
    }

    @GetMapping("/{cartId}/value")
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public AppResponse<BigDecimal> getOrderValue(@PathVariable("cartId") Long pCartId) {
        return mvCHelper.success(mvCartService.getCartValuePreDiscount(pCartId));
    }
}