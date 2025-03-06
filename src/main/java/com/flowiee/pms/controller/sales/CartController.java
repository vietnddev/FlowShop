package com.flowiee.pms.controller.sales;

import com.flowiee.pms.base.BaseController;
import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.model.payload.CartReq;
import com.flowiee.pms.service.sales.CartService;
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

    @PostMapping("/add-items")
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public AppResponse<String> addItemsToCart(@RequestBody CartReq request) {
        cartService.addItemsToCart(request);
        return success(null, "Success!");
    }
}