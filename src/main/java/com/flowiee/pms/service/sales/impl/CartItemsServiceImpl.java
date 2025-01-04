package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.entity.product.ProductCombo;
import com.flowiee.pms.entity.sales.Items;
import com.flowiee.pms.entity.sales.OrderCart;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.model.CartItemModel;
import com.flowiee.pms.model.ProductVariantParameter;
import com.flowiee.pms.model.dto.ProductVariantDTO;
import com.flowiee.pms.repository.sales.CartItemsRepository;
import com.flowiee.pms.repository.sales.OrderCartRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.product.ProductComboService;
import com.flowiee.pms.service.product.ProductVariantService;
import com.flowiee.pms.service.sales.CartItemsService;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.enumeration.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CartItemsServiceImpl extends BaseService implements CartItemsService {
    OrderCartRepository cartRepository;
    CartItemsRepository mvCartItemsRepository;
    ProductComboService mvProductComboService;
    ProductVariantService mvProductVariantService;

    @Override
    public List<Items> findAll() {
        return mvCartItemsRepository.findAll();
    }

    @Override
    public Items findById(Long itemId, boolean pThrowException) {
        Optional<Items> entityOptional = mvCartItemsRepository.findById(itemId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"cart item"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public List<CartItemModel> findAllItemsForSales() {
        List<CartItemModel> cartItemModelList = new ArrayList<>();
        OrderCart cart = cartRepository.findByAccountId(CommonUtils.getUserPrincipal().getId()).get(0);
        List<ProductCombo> productCombos = mvProductComboService.findAll();
        List<ProductVariantDTO> productVariantDTOs = mvProductVariantService.findAll(ProductVariantParameter.builder()
                .availableForSales(true)
                .checkInAnyCart(false)
                .build()
        ).getContent();
        for (ProductCombo productCbo : productCombos) {
            int availableQty = productCbo.getQuantity();
            if (availableQty < 1)
                continue;
            cartItemModelList.add(CartItemModel.builder()
                    .itemId(productCbo.getId())
                    .productComboId(productCbo.getId())
                    .productVariantId(-1l)
                    .itemName("[Cb] " + productCbo.getComboName() + " - còn " + availableQty)
                    .build());
        }
        Long cartId = cart.getId();
        for (ProductVariantDTO productVrt : productVariantDTOs) {
            Long productVariantId = productVrt.getId();
            int availableSalesQty = productVrt.getAvailableSalesQty();
            if (availableSalesQty < 1) {
                continue;
            }
            Items item = findItemByCartAndProductVariant(cartId, productVariantId);// item in cart
            if (item != null) {
                if (findQuantityOfItemProduct(cartId, productVariantId) >= availableSalesQty) {
                    continue;
                }
            }
            cartItemModelList.add(CartItemModel.builder()
                    .itemId(productVariantId)
                    .productComboId(-1l)
                    .productVariantId(productVariantId)
                    .itemName(new StringBuilder(productVrt.getVariantName()).append(" - còn ").append(availableSalesQty).toString())
                    .build());
        }
        return cartItemModelList;
    }

    @Override
    public Integer findQuantityOfItemProduct(Long cartId, Long productVariantId) {
        return mvCartItemsRepository.findQuantityByProductVariantId(cartId, productVariantId);
    }

    @Override
    public Integer findQuantityOfItemCombo(Long cartId, Long comboId) {
        return mvCartItemsRepository.findQuantityByProductVariantId(cartId, comboId);//It is wrong now, will fix in the future
    }

    @Override
    public Items findItemByCartAndProductVariant(Long cartId, Long productVariantId) {
        return mvCartItemsRepository.findByCartAndProductVariant(cartId, productVariantId);
    }

    @Override
    public Items save(Items items) {
        if (items == null || items.getOrderCart() == null || items.getProductDetail() == null) {
            throw new BadRequestException();
        }
        return mvCartItemsRepository.save(items);
    }

    @Override
    public Items update(Items entity, Long entityId) {
        if (entity == null || entityId == null || entityId <= 0) {
            throw new BadRequestException();
        }
        entity.setId(entityId);
        return mvCartItemsRepository.save(entity);
    }

    @Override
    public String delete(Long itemId) {
        if (this.findById(itemId, true) == null) {
            throw new BadRequestException();
        }
        mvCartItemsRepository.deleteById(itemId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Transactional
    @Override
    public void increaseItemQtyInCart(Long itemId, int quantity) {
        mvCartItemsRepository.updateItemQty(itemId, quantity);
    }

    @Transactional
    @Override
    public void deleteAllItems(Long cartId) {
        mvCartItemsRepository.deleteAllItems(cartId);
    }
}