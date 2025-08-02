package com.flowiee.pms.modules.sales.service;

import java.math.BigDecimal;
import java.util.List;

import com.flowiee.pms.modules.sales.dto.ItemsDTO;
import com.flowiee.pms.modules.sales.dto.OrderCartDTO;
import com.flowiee.pms.modules.sales.entity.Items;
import com.flowiee.pms.modules.sales.model.CartReq;
import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.sales.entity.OrderCart;

public interface CartService extends ICurdService<OrderCartDTO> {
    OrderCartDTO addDraftCart();

    OrderCart findEntById(Long id, boolean pThrowException);

    OrderCartDTO findDtoById(Long id, boolean pThrowException);

    List<OrderCartDTO> findCurrentUserCarts();

    List<OrderCart> findCartByAccountId(Long accountId);

    BigDecimal calTotalAmountWithoutDiscount(long cartId);

    boolean isItemExistsInCart(Long cartId, Long productVariantId);

    List<Items> getItems(Long cartId, List<Long> productVariantIds);

    void resetCart(Long cartId);

    void addItemsToCart(Long cartId, String[] productVariantIds);

    void addItemsToCart(CartReq cartReq);

    void updateItemsOfCart(Items items, Long itemId);

    List<ItemsDTO> findItems(Long pCartId);

    String deleteItem(Long pCartId, Long pItemId);

    ItemsDTO updateItemQuantity(Long pCartId, Long pItemId, Integer pQuantity);

    BigDecimal getCartValuePreDiscount(Long pCartId);

    void markOrderFinished(Long pCartId);
}