package com.flowiee.pms.cart.service;

import com.flowiee.pms.cart.model.CartItemModel;
import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.cart.entity.Items;
import com.flowiee.pms.cart.dto.ItemsDTO;

import java.util.List;

public interface CartItemsService extends ICurdService<ItemsDTO> {
    Items findEntById(Long pId, boolean throwException);

    List<CartItemModel> findAllItemsForSales();

    Integer findQuantityOfItemProduct(Long cartId, Long productVariantId);

    Integer findQuantityOfItemCombo(Long cartId, Long comboId);

    Items findItemByCartAndProductVariant(Long cartId, Long productVariantId);

    void increaseItemQtyInCart(Long itemId, int quantity);

    void deleteAllItems(Long cartId);
}