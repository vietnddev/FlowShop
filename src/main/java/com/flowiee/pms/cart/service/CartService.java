package com.flowiee.pms.cart.service;

import java.math.BigDecimal;
import java.util.List;

import com.flowiee.pms.cart.dto.ItemsDTO;
import com.flowiee.pms.order.dto.OrderCartDTO;
import com.flowiee.pms.cart.entity.Items;
import com.flowiee.pms.cart.model.CartReq;
import com.flowiee.pms.cart.entity.OrderCart;
import com.flowiee.pms.shared.base.DeleteService;

public interface CartService extends DeleteService {
    OrderCartDTO addDraftCart();

    OrderCart findEntById(Long id, boolean pThrowException);

    OrderCartDTO findDtoById(Long id, boolean pThrowException);

    List<OrderCartDTO> findCurrentUserCarts();

    List<OrderCart> findCartByAccountId(Long accountId);

    List<Items> getItems(Long cartId, List<Long> productVariantIds);

    void resetCart(Long cartId);

    void addItemsToCart(CartReq cartReq);

    void updateItemsOfCart(ItemsDTO items, Long itemId);

    String deleteItem(Long pCartId, Long pItemId);

    ItemsDTO updateItemQuantity(Long pCartId, Long pItemId, Integer pQuantity);

    BigDecimal getCartValuePreDiscount(Long pCartId);
}