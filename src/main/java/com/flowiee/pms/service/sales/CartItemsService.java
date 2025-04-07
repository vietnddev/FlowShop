package com.flowiee.pms.service.sales;

import com.flowiee.pms.model.CartItemModel;
import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.entity.sales.Items;
import com.flowiee.pms.model.dto.ItemsDTO;

import java.util.List;

public interface CartItemsService extends BaseCurdService<ItemsDTO> {
    List<CartItemModel> findAllItemsForSales();

    Integer findQuantityOfItemProduct(Long cartId, Long productVariantId);

    Integer findQuantityOfItemCombo(Long cartId, Long comboId);

    Items findItemByCartAndProductVariant(Long cartId, Long productVariantId);

    void increaseItemQtyInCart(Long itemId, int quantity);

    void deleteAllItems(Long cartId);
}