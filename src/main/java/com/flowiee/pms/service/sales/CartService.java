package com.flowiee.pms.service.sales;

import java.util.List;

import com.flowiee.pms.entity.sales.Items;
import com.flowiee.pms.model.payload.CartReq;
import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.entity.sales.OrderCart;

public interface CartService extends BaseCurdService<OrderCart> {
    List<OrderCart> findCartByAccountId(Long accountId);

    Double calTotalAmountWithoutDiscount(long cartId);

    boolean isItemExistsInCart(Long cartId, Long productVariantId);

    List<Items> getItems(Long cartId, List<Long> productVariantIds);

    void resetCart(Long cartId);

    void addItemsToCart(Long cartId, String[] productVariantIds);

    void addItemsToCart(CartReq cartReq);

    void updateItemsOfCart(Items items, Long itemId);
}