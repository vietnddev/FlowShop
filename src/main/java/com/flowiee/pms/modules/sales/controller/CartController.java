package com.flowiee.pms.modules.sales.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.base.controller.ControllerHelper;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.sales.model.CartReq;
import com.flowiee.pms.modules.sales.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api.prefix}/sls/cart")
@Tag(name = "Cart API", description = "Quản lý giỏ hàng")
@RequiredArgsConstructor
public class CartController extends BaseController {
    private final CartService cartService;
    private final ControllerHelper mvCHelper;

    @PostMapping("/add-items")
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public AppResponse<String> addItemsToCart(@RequestBody CartReq request) {
        cartService.addItemsToCart(request);
        return mvCHelper.success(null, "Success!");
    }
}