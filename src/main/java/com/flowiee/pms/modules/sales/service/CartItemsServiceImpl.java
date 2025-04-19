package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.common.base.service.BaseGService;
import com.flowiee.pms.modules.sales.entity.Items;
import com.flowiee.pms.modules.sales.entity.OrderCart;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.sales.model.CartItemModel;
import com.flowiee.pms.modules.product.model.ProductVariantParameter;
import com.flowiee.pms.modules.sales.dto.ItemsDTO;
import com.flowiee.pms.modules.product.dto.ProductComboDTO;
import com.flowiee.pms.modules.product.dto.ProductVariantDTO;
import com.flowiee.pms.modules.sales.repository.CartItemsRepository;
import com.flowiee.pms.modules.sales.repository.OrderCartRepository;
import com.flowiee.pms.common.security.UserSession;
import com.flowiee.pms.modules.product.service.ProductComboService;
import com.flowiee.pms.modules.product.service.ProductVariantService;
import com.flowiee.pms.common.enumeration.MessageCode;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartItemsServiceImpl extends BaseGService<Items, ItemsDTO, CartItemsRepository> implements CartItemsService {
    private final OrderCartRepository cartRepository;
    private final ProductComboService mvProductComboService;
    private final ProductVariantService mvProductVariantService;
    private final UserSession userSession;

    public CartItemsServiceImpl(CartItemsRepository pCartItemsRepository, OrderCartRepository pCartRepository,
                                ProductComboService pProductComboService, ProductVariantService pProductVariantService,
                                UserSession userSession) {
        super(Items.class, ItemsDTO.class, pCartItemsRepository);
        this.cartRepository = pCartRepository;
        this.mvProductComboService = pProductComboService;
        this.mvProductVariantService = pProductVariantService;
        this.userSession = userSession;
    }

    @Override
    public List<ItemsDTO> findAll() {
        return super.findAll();
    }

    @Override
    public ItemsDTO findById(Long pItemId, boolean pThrowException) {
        return super.findById(pItemId, pThrowException);
    }

    @Override
    public List<CartItemModel> findAllItemsForSales() {
        List<CartItemModel> cartItemModelList = new ArrayList<>();
        OrderCart cart = cartRepository.findByAccountId(userSession.getUserPrincipal().getId()).get(0);
        List<ProductComboDTO> productCombos = mvProductComboService.findAll();
        List<ProductVariantDTO> productVariantDTOs = mvProductVariantService.findAll(ProductVariantParameter.builder()
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
        if (pDto == null || pDto.getOrderCart() == null || pDto.getProductDetail() == null) {
            throw new BadRequestException();
        }
        return super.save(pDto);
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
        if (this.findById(itemId, true) == null) {
            throw new BadRequestException();
        }
        mvEntityRepository.deleteById(itemId);
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