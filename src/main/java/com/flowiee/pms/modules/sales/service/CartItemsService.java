package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.modules.sales.model.CartItemModel;
import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.sales.entity.Items;
import com.flowiee.pms.modules.sales.dto.ItemsDTO;

import java.util.List;

public interface CartItemsService extends ICurdService<ItemsDTO> {
    List<CartItemModel> findAllItemsForSales();

    Integer findQuantityOfItemProduct(Long cartId, Long productVariantId);

    Integer findQuantityOfItemCombo(Long cartId, Long comboId);

    Items findItemByCartAndProductVariant(Long cartId, Long productVariantId);

    void increaseItemQtyInCart(Long itemId, int quantity);

    void deleteAllItems(Long cartId);
}