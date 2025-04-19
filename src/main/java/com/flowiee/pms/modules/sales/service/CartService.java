package com.flowiee.pms.modules.sales.service;

import java.util.List;

import com.flowiee.pms.modules.sales.entity.Items;
import com.flowiee.pms.modules.sales.model.CartReq;
import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.sales.entity.OrderCart;

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