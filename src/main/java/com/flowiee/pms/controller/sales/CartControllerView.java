package com.flowiee.pms.controller.sales;

import com.flowiee.pms.base.controller.BaseController;
import com.flowiee.pms.entity.sales.Items;
import com.flowiee.pms.entity.sales.OrderCart;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.ResourceNotFoundException;
import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.model.CartItemModel;
import com.flowiee.pms.service.category.CategoryService;
import com.flowiee.pms.service.product.ProductVariantService;
import com.flowiee.pms.service.sales.CartItemsService;
import com.flowiee.pms.service.sales.CartService;
import com.flowiee.pms.service.system.AccountService;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.enumeration.OrderStatus;
import com.flowiee.pms.common.enumeration.Pages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

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
    ProductVariantService mvProductVariantService;

    @GetMapping("/ban-hang")
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public ModelAndView showPageBanHang() {
        ModelAndView modelAndView = new ModelAndView(Pages.PRO_ORDER_SELL.getTemplate());
        List<OrderCart> orderCartCurrent = mvCartService.findCartByAccountId(CommonUtils.getUserPrincipal().getId());
        if (orderCartCurrent.isEmpty()) {
            OrderCart orderCart = new OrderCart();
            orderCart.setCreatedBy(CommonUtils.getUserPrincipal().getId());
            mvCartService.save(orderCart);
        }

        List<OrderCart> listOrderCart = mvCartService.findCartByAccountId(CommonUtils.getUserPrincipal().getId());
        modelAndView.addObject("listCart", listOrderCart);
        modelAndView.addObject("listAccount", mvAccountService.findAll());
        modelAndView.addObject("listSalesChannel", mvCategoryService.findSalesChannels());
        modelAndView.addObject("listPaymentMethod", mvCategoryService.findPaymentMethods());
        modelAndView.addObject("listOrderStatus", mvCategoryService.findOrderStatus(null));
        //modelAndView.addObject("listProductVariant", mvProductVariantService.findAll(-1, -1, null, null, null, null, null, true).getContent());
        //modelAndView.addObject("listItemsForSales", mvCartItemsService.findAllItemsForSales());
        //modelAndView.addObject("listItemsForSales", List.of(CartItemModel.builder().build()));
        modelAndView.addObject("orderStatusMap", OrderStatus.getAllMap(null));

        double totalAmountWithoutDiscount = mvCartService.calTotalAmountWithoutDiscount(listOrderCart.get(0).getId());
        double amountDiscount = 0;
        double totalAmountDiscount = totalAmountWithoutDiscount - amountDiscount;
        modelAndView.addObject("totalAmountWithoutDiscount", totalAmountWithoutDiscount);
        //modelAndView.addObject("amountDiscount", amountDiscount);
        modelAndView.addObject("totalAmountDiscount", totalAmountDiscount);
        return baseView(modelAndView);
    }

    @Operation(summary = "Get all items available for sales")
    @GetMapping("/cart/product/available-sales")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public AppResponse<List<CartItemModel>> getAllItemsForSales() {
        return success(mvCartItemsService.findAllItemsForSales());
    }

    @PostMapping("/ban-hang/cart/item/add")
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public ModelAndView addItemsToCart(@RequestParam("cartId") Long cartId, @RequestParam("bienTheSanPhamId") String[] bienTheSanPhamId) {
        OrderCart cart = mvCartService.findById(cartId, true);
        mvCartService.addItemsToCart(cart.getId(), bienTheSanPhamId);
        return new ModelAndView("redirect:/sls/order/ban-hang");
    }

    @PostMapping("/ban-hang/cart/item/update/{itemId}")
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public ModelAndView updateItemsOfCart(@RequestParam("cartId") Long cartId,
                                          @ModelAttribute("items") Items items,
                                          @PathVariable("itemId") Long itemId) {
        if (mvCartService.findById(cartId, true) == null) {
            throw new ResourceNotFoundException("Cart not found!");
        }
        mvCartService.updateItemsOfCart(items, itemId);
        return new ModelAndView("redirect:/sls/order/ban-hang");
    }

    @PostMapping("/ban-hang/cart/item/delete/{itemId}")
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public ModelAndView deleteItemsOfCart(@RequestParam("cartId") Long cartId, @PathVariable("itemId") Long itemId) {
        if (mvCartService.findById(cartId, false) == null) {
            throw new BadRequestException("Sản phẩm cần xóa trong giỏ hàng không tồn tại! cartId=" + cartId + ", itemId=" + itemId);
        }
        mvCartItemsService.delete(itemId);
        return new ModelAndView("redirect:/sls/order/ban-hang");
    }

    @PostMapping("/ban-hang/cart/{cartId}/reset")
    @PreAuthorize("@vldModuleSales.insertOrder(true)")
    public ModelAndView resetCart(@PathVariable("cartId") Long cartId) {
        if (mvCartService.findById(cartId, true) == null) {
            throw new BadRequestException("Cart not found! cartId=" + cartId);
        }
        mvCartService.resetCart(cartId);
        return new ModelAndView("redirect:/sls/order/ban-hang");
    }

//    @PutMapping("/cart/{cartId}/item/update/{itemId}")
//    @PreAuthorize("@vldModuleSales.insertOrder(true)")
//    public AppResponse<Items> updateItemsOfCart(@RequestBody Items items,
//                                                @PathVariable("cartId") Integer cartId,
//                                                @PathVariable("itemId") Integer itemId) {
//        try {
//            if (cartId <= 0 || cartService.findById(cartId).isEmpty()) {
//                throw new BadRequestException();
//            }
//            if (itemId <= 0 || cartItemsService.findById(itemId).isEmpty()) {
//                throw new BadRequestException();
//            }
//            items.setId(itemId);
//            items.setOrderCart(cartService.findById(cartId).get());
//            if (items.getQuantity() > 0) {
//                cartItemsService.save(items);
//            } else {
//                cartItemsService.delete(items.getId());
//            }
//            return success(null);
//        } catch (RuntimeException ex) {
//            throw new AppException(String.format(MessageUtils.UPDATE_ERROR_OCCURRED, "items"), ex);
//        }
//    }

//    @PostMapping("/cart/{cartId}/item/add")
//    @PreAuthorize("@vldModuleSales.insertOrder(true)")
//    public AppResponse<List<Items>> addItemsToCart(@RequestBody String[] bienTheSanPhamId, @PathVariable("cartId") Integer cartId) {
//        try {
//            if (cartId <= 0 || cartService.findById(cartId).isEmpty()) {
//                throw new BadRequestException();
//            }
//            List<String> listProductVariantId = Arrays.stream(bienTheSanPhamId).toList();
//            for (String productVariantId : Arrays.stream(bienTheSanPhamId).toList()) {
//                if (cartService.isItemExistsInCart(cartId, Integer.parseInt(productVariantId))) {
//                    Items items = cartItemsService.findItemByCartAndProductVariant(cartId, Integer.parseInt(productVariantId));
//                    cartItemsService.increaseItemQtyInCart(items.getId(), items.getQuantity() + 1);
//                } else {
//                    Items items = new Items();
//                    items.setOrderCart(new OrderCart(cartId));
//                    items.setProductDetail(new ProductDetail(Integer.parseInt(productVariantId)));
//                    items.setQuantity(1);
//                    items.setNote("");
//                    cartItemsService.save(items);
//                }
//            }
//            return success(null);
//        } catch (RuntimeException ex) {
//            throw new AppException(String.format(MessageUtils.CREATE_ERROR_OCCURRED, "items"), ex);
//        }
//    }

//    @DeleteMapping("/cart/{cartId}/item/delete/{itemId}")
//    @PreAuthorize("@vldModuleSales.insertOrder(true)")
//    public AppResponse<String> deleteItemsOfCart(@PathVariable("cartId") Integer cartId, @PathVariable("itemId") Integer itemId) {
//        try {
//            if (cartService.findById(cartId).isEmpty()) {
//                throw new BadRequestException();
//            }
//            return success(cartItemsService.delete(itemId));
//        } catch (RuntimeException ex) {
//            throw new AppException(String.format(MessageUtils.DELETE_ERROR_OCCURRED, "items"), ex);
//        }
//    }
}