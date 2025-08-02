package com.flowiee.pms.modules.sales.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.enumeration.PriceType;
import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.sales.entity.Items;
import com.flowiee.pms.modules.sales.entity.OrderCart;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.sales.model.CartItemModel;
import com.flowiee.pms.modules.inventory.model.ProductVariantSearchRequest;
import com.flowiee.pms.modules.sales.dto.ItemsDTO;
import com.flowiee.pms.modules.inventory.dto.ProductComboDTO;
import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import com.flowiee.pms.modules.sales.repository.CartItemsRepository;
import com.flowiee.pms.modules.sales.repository.OrderCartRepository;
import com.flowiee.pms.common.security.UserSession;
import com.flowiee.pms.modules.inventory.service.ProductComboService;
import com.flowiee.pms.modules.inventory.service.ProductVariantService;
import com.flowiee.pms.common.enumeration.MessageCode;
import com.flowiee.pms.modules.sales.service.CartItemsService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartItemsServiceImpl extends BaseService<Items, ItemsDTO, CartItemsRepository> implements CartItemsService {
    private final OrderCartRepository cartRepository;
    private final ProductComboService mvProductComboService;
    private final ProductVariantService mvProductVariantService;

    public CartItemsServiceImpl(CartItemsRepository pCartItemsRepository, OrderCartRepository pCartRepository,
                                ProductComboService pProductComboService, ProductVariantService pProductVariantService) {
        super(Items.class, ItemsDTO.class, pCartItemsRepository);
        this.cartRepository = pCartRepository;
        this.mvProductComboService = pProductComboService;
        this.mvProductVariantService = pProductVariantService;
    }

    @Override
    public List<ItemsDTO>find(BaseParameter pParam) {
        return super.find(pParam);
    }

    @Override
    public ItemsDTO findById(Long pItemId, boolean pThrowException) {
        return super.findDtoById(pItemId, pThrowException);
    }

    @Override
    public Items findEntById(Long pId, boolean throwException) {
        return super.findEntById(pId, throwException);
    }

    @Override
    public List<CartItemModel> findAllItemsForSales() {
        List<CartItemModel> cartItemModelList = new ArrayList<>();
        OrderCart cart = cartRepository.findByAccountId(getUserPrincipal().getId()).get(0);
        List<ProductComboDTO> productCombos = mvProductComboService.findAll(-1, -1).getContent();
        List<ProductVariantDTO> productVariantDTOs = mvProductVariantService.findAll(ProductVariantSearchRequest.builder()
                .availableForSales(true)
                .checkInAnyCart(false)
                .build()
        ).getContent();

        for (ProductComboDTO productCbo : productCombos) {
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
        return mvEntityRepository.findQuantityByProductVariantId(cartId, productVariantId);
    }

    @Override
    public Integer findQuantityOfItemCombo(Long cartId, Long comboId) {
        return mvEntityRepository.findQuantityByProductVariantId(cartId, comboId);//It is wrong now, will fix in the future
    }

    @Override
    public Items findItemByCartAndProductVariant(Long cartId, Long productVariantId) {
        return mvEntityRepository.findByCartAndProductVariant(cartId, productVariantId);
    }

    @Override
    public ItemsDTO save(ItemsDTO pDto) {
        if (pDto == null || pDto.getCartId() == null || pDto.getProductDetail() == null) {
            throw new BadRequestException();
        }

        OrderCart lvCart = new OrderCart();
        lvCart.setId(pDto.getCartId());

        Items lvItem = Items.builder()
                .orderCart(lvCart)
                .productDetail(new ProductDetail(pDto.getProductDetail().getId()))
                .priceType(PriceType.L.name())
                .price(pDto.getPrice())
                .priceOriginal(pDto.getPriceOriginal())
                .extraDiscount(BigDecimal.ZERO)
                .quantity(pDto.getQuantity())
                .note("")
                .build();

        return super.convertDTO(mvEntityRepository.save(lvItem));
    }

    @Override
    public ItemsDTO update(ItemsDTO pDto, Long entityId) {
        if (pDto == null || entityId == null || entityId <= 0) {
            throw new BadRequestException();
        }

        Items lvItem = super.findById(entityId).orElseThrow(() -> new BadRequestException());
        //lvItem.set...

        return convertDTO(mvEntityRepository.save(lvItem));
    }

    @Override
    public String delete(Long itemId) {
        super.delete(itemId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Transactional
    @Override
    public void increaseItemQtyInCart(Long itemId, int quantity) {
        mvEntityRepository.updateItemQty(itemId, quantity);
    }

    @Transactional
    @Override
    public void deleteAllItems(Long cartId) {
        mvEntityRepository.deleteAllItems(cartId);
    }
}