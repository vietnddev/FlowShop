package com.flowiee.pms.cart.controller;

import com.flowiee.pms.shared.base.BaseController;
import com.flowiee.pms.system.entity.Category;
import com.flowiee.pms.system.enums.CATEGORY;
import com.flowiee.pms.shared.exception.BadRequestException;
import com.flowiee.pms.shared.response.AppResponse;
import com.flowiee.pms.cart.model.CartItemModel;
import com.flowiee.pms.system.service.CategoryService;
import com.flowiee.pms.cart.service.CartItemsService;
import com.flowiee.pms.cart.service.CartService;
import com.flowiee.pms.system.service.AccountService;
import com.flowiee.pms.order.enums.OrderStatus;
import com.flowiee.pms.shared.enums.Pages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sls/order")
@Tag(name = "Order API", description = "Quản lý giỏ hàng")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CartControllerView extends BaseController {
    CartService mvCartService;
    AccountService mvAccountService;
    CategoryService mvCategoryService;
    CartItemsService mvCartItemsService;

    @GetMapping("/ban-hang")
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public ModelAndView showPageBanHang() {
        ModelAndView modelAndView = new ModelAndView(Pages.PRO_ORDER_SELL.getTemplate());

        Map<CATEGORY, List<Category>> lvCategories = mvCategoryService.findByType(List.of(
                CATEGORY.SALES_CHANNEL, CATEGORY.PAYMENT_METHOD, CATEGORY.SHIP_METHOD));

        modelAndView.addObject("listAccount", mvAccountService.find());
        modelAndView.addObject("listSalesChannel", lvCategories.get(CATEGORY.SALES_CHANNEL));
        modelAndView.addObject("listPaymentMethod", lvCategories.get(CATEGORY.PAYMENT_METHOD));
        modelAndView.addObject("listDeliveryType", lvCategories.get(CATEGORY.SHIP_METHOD));
        modelAndView.addObject("orderStatusMap", OrderStatus.getAllMap(null));
        modelAndView.addObject("totalAmountWithoutDiscount", 0);
        modelAndView.addObject("totalAmountDiscount", 0);
        return baseView(modelAndView);
    }

    @Operation(summary = "Get all items available for sales")
    @GetMapping("/cart/product/available-sales")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public AppResponse<List<CartItemModel>> getAllItemsForSales() {
        return AppResponse.success(mvCartItemsService.findAllItemsForSales());
    }

    @PostMapping("/ban-hang/cart/{cartId}/reset")
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public ModelAndView resetCart(@PathVariable("cartId") Long cartId) {
        if (mvCartService.findEntById(cartId, true) == null) {
            throw new BadRequestException("Cart not found! cartId=" + cartId);
        }
        mvCartService.resetCart(cartId);
        return new ModelAndView("redirect:/sls/order/ban-hang");
    }
}