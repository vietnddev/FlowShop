package com.flowiee.pms.modules.sales.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.common.security.UserSession;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.PriceUtils;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.inventory.entity.ProductPrice;
import com.flowiee.pms.modules.sales.dto.OrderCartDTO;
import com.flowiee.pms.modules.sales.entity.Items;
import com.flowiee.pms.modules.sales.entity.OrderCart;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.sales.dto.ItemsDTO;
import com.flowiee.pms.modules.sales.model.CartItemsReq;
import com.flowiee.pms.modules.sales.model.CartReq;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import com.flowiee.pms.modules.inventory.repository.ProductPriceRepository;
import com.flowiee.pms.modules.sales.repository.CartItemsRepository;
import com.flowiee.pms.modules.sales.repository.OrderCartRepository;
import com.flowiee.pms.modules.inventory.service.ProductVariantService;

import com.flowiee.pms.modules.sales.service.CartItemsService;
import com.flowiee.pms.modules.sales.service.CartService;
import com.flowiee.pms.modules.system.service.SystemLogService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartServiceImpl extends BaseService<OrderCart, OrderCartDTO, OrderCartRepository> implements CartService {
    CartItemsService mvCartItemsService;
    OrderCartRepository mvCartRepository;
    CartItemsRepository mvCartItemsRepository;
    ProductVariantService mvProductVariantService;
    ProductPriceRepository mvProductPriceRepository;
    ModelMapper mvModelMapper;
    SystemLogService systemLogService;
    UserSession mvUserSession;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public CartServiceImpl(OrderCartRepository pCartRepository, CartItemsService pCartItemsService, CartItemsRepository pCartItemsRepository, ProductVariantService pProductVariantService, ProductPriceRepository pProductPriceRepository, ModelMapper pModelMapper, SystemLogService pSystemLogService, UserSession pUserSession) {
        super(OrderCart.class, OrderCartDTO.class, pCartRepository);
        this.mvCartItemsService = pCartItemsService;
        this.mvCartRepository = pCartRepository;
        this.mvCartItemsRepository = pCartItemsRepository;
        this.mvProductVariantService = pProductVariantService;
        this.mvProductPriceRepository = pProductPriceRepository;
        this.mvModelMapper = pModelMapper;
        this.systemLogService = pSystemLogService;
        this.mvUserSession = pUserSession;
    }

    @Override
    public OrderCartDTO addDraftCart() {
        OrderCartDTO lvCartDto = new OrderCartDTO();
        lvCartDto.setIsFinish(false);
        lvCartDto.setCreatedBy(mvUserSession.getUserPrincipal().getId());
        return super.save(lvCartDto);
    }

    @Override
    public List<OrderCart> findCartByAccountId(Long accountId) {
        List<OrderCart> listCart = mvCartRepository.findByAccountId(accountId);
        if (CollectionUtils.isEmpty(listCart)) {
            return List.of();
        }
        for (OrderCart cart : listCart) {
            if (CollectionUtils.isEmpty(cart.getListItems())) {
                continue;
            }
            for (Items item : cart.getListItems()) {
                //ProductPrice itemPrice = item.getProductDetail().getVariantPrice();
                List<ProductPrice> itemPrice = mvProductPriceRepository.findPresentPrices(item.getProductDetail().getId());
                if (itemPrice != null) {
                    BigDecimal lvRetailPrice = PriceUtils.getPriceValue(itemPrice, com.flowiee.pms.modules.inventory.enums.PriceType.RTL);
                    BigDecimal lvWholesalePrice = PriceUtils.getPriceValue(itemPrice, com.flowiee.pms.modules.inventory.enums.PriceType.WHO);
                    PriceType priceType = PriceType.valueOf(item.getPriceType());
                    if (priceType.equals(PriceType.L)) {
                        item.setPriceOriginal(lvRetailPrice);
                        item.setPrice(lvRetailPrice);
                    }
                    if (priceType.equals(PriceType.S)) {
                        item.setPriceOriginal(lvWholesalePrice);
                        item.setPrice(lvWholesalePrice);
                    }
                }
                item.getProductDetail().setAvailableSalesQty(item.getProductDetail().getStorageQty() - item.getProductDetail().getDefectiveQty());
            }
        }
        return listCart;
    }

    @Override
    public List<OrderCartDTO>find(BaseParameter pParam) {
        return super.find(pParam);
    }

    @Override
    public OrderCartDTO findById(Long id, boolean pThrowException) {
        return super.findDtoById(id, pThrowException);
    }

    @Override
    public OrderCart findEntById(Long pId, boolean throwException) {
        return super.findEntById(pId, throwException);
    }

    @Override
    public OrderCartDTO findDtoById(Long pId, boolean throwException) {
        OrderCart lvCart = super.findEntById(pId, throwException);

        List<Items> lvItems = lvCart.getListItems();
        List<ItemsDTO> lvItemDTOs = new ArrayList<>();
        if (lvItems != null) {
            for (Items lvItem : lvItems) {
                ProductDetail lvProductVariant = lvItem.getProductDetail();

                ItemsDTO lvItemDTO = new ItemsDTO();
                lvItemDTO.setCartId(lvCart.getId());
                lvItemDTO.setItemId(lvItem.getId());
                lvItemDTO.setItemName(lvProductVariant.getVariantName());
                lvItemDTO.setPrice(lvItem.getPrice());
                lvItemDTO.setQuantity(lvItem.getQuantity());
                lvItemDTO.setExtraDiscount(lvItem.getExtraDiscount());
                lvItemDTO.setNote(lvItem.getNote());
                //(Price * Quantity) - Extra Discount
                BigDecimal lvSubTotal = (lvItemDTO.getPrice().multiply(BigDecimal.valueOf(lvItem.getQuantity()))).subtract(lvItemDTO.getExtraDiscount());
                lvItemDTO.setSubTotal(lvSubTotal);

                lvItemDTOs.add(lvItemDTO);
            }
        }

        OrderCartDTO lvCartDto = new OrderCartDTO();
        lvCartDto.setId(lvCart.getId());
        lvCartDto.setSalesChannelId(0l);
        lvCartDto.setPaymentMethodId(0l);
        lvCartDto.setItems(lvItemDTOs);

        return lvCartDto;
    }

    @Override
    public List<OrderCartDTO> findCurrentUserCarts() {
        List<OrderCart> lvCurrentUserCarts = mvCartRepository.findByAccountId(mvUserSession.getUserPrincipal().getId());
        List<OrderCartDTO> lvCartDTOs = new ArrayList<>();
        for (OrderCart lvCart : lvCurrentUserCarts) {
            OrderCartDTO lvCartDto = new OrderCartDTO();
            lvCartDto.setId(lvCart.getId());
            lvCartDto.setCreatedBy(lvCart.getCreatedBy());
            lvCartDTOs.add(lvCartDto);
        }
        return lvCartDTOs;
    }

    @Override
    public OrderCartDTO save(OrderCartDTO orderCart) {
        return super.save(orderCart);
    }

    @Override
    public OrderCartDTO update(OrderCartDTO cart, Long cartId) {
        return super.update(cart, cartId);
    }

    @Transactional
    @Override
    public String delete(Long cartId) {
        OrderCart cart = super.findEntById(cartId, true);
        mvCartItemsRepository.deleteAllItems(cart.getId());
        mvCartRepository.deleteById(cartId);

        systemLogService.writeLogDelete(MODULE.PRODUCT, ACTION.PRO_CART_C, MasterObject.Cart, "Xóa/Reset giỏ hàng", "cartId = " + cartId);

        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public BigDecimal calTotalAmountWithoutDiscount(long cartId) {
        return mvCartItemsRepository.calTotalAmountWithoutDiscount(cartId);
    }

    @Override
    public boolean isItemExistsInCart(Long cartId, Long productVariantId) {
        Items item = mvCartItemsRepository.findByCartAndProductVariant(cartId, productVariantId);
        return item != null;
    }

    @Override
    public List<Items> getItems(Long cartId, List<Long> productVariantIds) {
        if (CollectionUtils.isEmpty(productVariantIds)) {
            return List.of();
        }

        List<Items> lvItemList = new ArrayList<>();
        int batchSize = 1000; // Số lượng tối đa trong một truy vấn
        for (int i = 0; i < productVariantIds.size(); i += batchSize) {
            List<Long> batch = new ArrayList<>(productVariantIds.subList(i, Math.min(i + batchSize, productVariantIds.size())));
            lvItemList.addAll(mvCartItemsRepository.findItems(cartId, batch));
        }

        return lvItemList;
    }

    @Transactional
    @Override
    public void resetCart(Long cartId) {
        OrderCart cart = super.findEntById(cartId, true);
        mvCartItemsRepository.deleteAllItems(cart.getId());
    }

    @Transactional
    @Override
    public void addItemsToCart(CartReq cartReq) {
        Long lvCartId = cartReq.getCartId();
        OrderCart orderCart = super.findEntById(lvCartId, true);

        List<CartItemsReq> itemsList = cartReq.getItems();
        if (ObjectUtils.isEmpty(itemsList)) {
            throw new BadRequestException("Please choose at least one product!");
        }

        for (CartItemsReq item : itemsList) {
            ProductVariantDTO lvProductVariant = mvProductVariantService.findById(item.getProductVariantId(), false);
            if (lvProductVariant == null) {
                continue;
            }
            Integer lvItemQty = CoreUtils.coalesce(item.getQuantity(), 1);
            if (lvItemQty <= 0) {
                throw new BadRequestException("Vui lòng nhập số lượng cho sản phẩm: " + lvProductVariant.getVariantName());
            }
            if (lvProductVariant.getAvailableSalesQty() == 0 || lvProductVariant.getAvailableSalesQty() < lvItemQty) {
                throw new AppException(ErrorCode.ProductOutOfStock, new Object[]{lvProductVariant.getVariantName()}, null, getClass(), null);
            }

            List<ProductPrice> productVariantPrice = mvProductPriceRepository.findPresentPrices(lvProductVariant.getId());
            if (productVariantPrice == null) {
                throw new AppException(String.format("Sản phẩm %s chưa được thiết lập giá bán!", lvProductVariant.getVariantName()));
            }
            BigDecimal lvRetailPrice = PriceUtils.getPriceValue(productVariantPrice, com.flowiee.pms.modules.inventory.enums.PriceType.RTL);
            BigDecimal lvRetailPriceDiscount = PriceUtils.getPriceValue(productVariantPrice, com.flowiee.pms.modules.inventory.enums.PriceType.RTL);

            BigDecimal lvItemPrice = lvRetailPriceDiscount != null ? lvRetailPriceDiscount : lvRetailPrice;
            BigDecimal lvItemOriginalPrice = lvRetailPrice;

            if (this.isItemExistsInCart(lvCartId, lvProductVariant.getId())) {
                Items items = mvCartItemsService.findItemByCartAndProductVariant(lvCartId, lvProductVariant.getId());
                //mvCartItemsService.increaseItemQtyInCart(items.getId(), items.getQuantity() + 1);
                items.setQuantity(items.getQuantity() + lvItemQty);
                items.setPriceType(PriceType.L.name());
                items.setPrice(lvItemPrice);
                items.setPriceOriginal(lvItemOriginalPrice);
                mvCartItemsRepository.save(items);
            } else {
                ItemsDTO itemsDto = mvModelMapper.map(Items.builder()
                        .orderCart(orderCart)
                        .productDetail(new ProductDetail(lvProductVariant.getId()))
                        .priceType(PriceType.L.name())
                        .price(lvItemPrice)
                        .priceOriginal(lvItemOriginalPrice)
                        .extraDiscount(BigDecimal.ZERO)
                        .quantity(lvItemQty)
                        .note("")
                        .build(),
                        ItemsDTO.class);
                itemsDto.setCartId(orderCart.getId());
                mvCartItemsService.save(itemsDto);
            }
        }
    }

    @Override
    public void updateItemsOfCart(ItemsDTO pItemToUpdate, Long itemId) {
        Items lvItem = mvCartItemsService.findEntById(itemId, true);
        if (pItemToUpdate.getQuantity() <= 0) {
            mvCartItemsService.delete(lvItem.getId());
        } else {
            ProductDetail productVariant = lvItem.getProductDetail();
            if (pItemToUpdate.getQuantity() > productVariant.getAvailableSalesQty()) {
                throw new AppException(ErrorCode.ProductOutOfStock, new Object[]{productVariant.getVariantName()}, null, getClass(), null);
            }

            List<ProductPrice> productVariantPrice = mvProductPriceRepository.findPresentPrices(productVariant.getId());
            if (productVariantPrice == null) {
                throw new AppException(String.format("Sản phẩm %s chưa được thiết lập giá bán!", productVariant.getVariantName()));
            }
            BigDecimal lvRetailPrice = PriceUtils.getPriceValue(productVariantPrice, com.flowiee.pms.modules.inventory.enums.PriceType.RTL);
            BigDecimal lvRetailPriceDiscount = PriceUtils.getPriceValue(productVariantPrice, com.flowiee.pms.modules.inventory.enums.PriceType.RTL);
            BigDecimal lvWholesalePrice = PriceUtils.getPriceValue(productVariantPrice, com.flowiee.pms.modules.inventory.enums.PriceType.WHO);
            BigDecimal lvWholesalePriceDiscount = PriceUtils.getPriceValue(productVariantPrice, com.flowiee.pms.modules.inventory.enums.PriceType.WHO);

            //String lvPriceType = pItemToUpdate.getPriceType();
            String lvPriceType = PriceType.L.name();

            lvItem.setNote(pItemToUpdate.getNote());
            lvItem.setQuantity(pItemToUpdate.getQuantity());
            if (!lvItem.getPriceType().equals(lvPriceType)) {
                if (lvPriceType.equals(PriceType.L.name())) {
                    lvItem.setPrice(lvRetailPriceDiscount);
                    lvItem.setPriceOriginal(lvRetailPrice);
                    //lvItem.setPriceType(PriceType.L.name());
                }
                if (lvPriceType.equals(PriceType.S.name())) {
                    lvItem.setPrice(lvWholesalePriceDiscount);
                    lvItem.setPriceOriginal(lvWholesalePrice);
                    //lvItem.setPriceType(PriceType.S.name());
                }
            }
            if (pItemToUpdate.getExtraDiscount() != null) {
                lvItem.setExtraDiscount(pItemToUpdate.getExtraDiscount());
            }

            mvCartItemsRepository.save(lvItem);
        }
    }

    @Override
    public List<ItemsDTO> findItems(Long pCartId) {
        OrderCart lvCart = super.findEntById(pCartId, true);

        List<Items> lvItems = lvCart.getListItems();
        if (CollectionUtils.isEmpty(lvItems)) {
            return List.of();
        }

        List<ItemsDTO> lvItemDTOs = new ArrayList<>();
        for (Items lvItem : lvItems) {
            ItemsDTO lvItemDto = new ItemsDTO();

            List<ProductPrice> lvItemPrices = mvProductPriceRepository.findPresentPrices(lvItem.getProductDetail().getId());

            ProductVariantDTO lvProductVariantDto = new ProductVariantDTO();
            lvProductVariantDto.setId(lvItem.getProductDetail().getId());

            lvItemDto.setProductDetail(lvProductVariantDto);
            lvItemDto.setQuantity(lvItem.getQuantity());
            lvItemDto.setNote(lvItem.getNote());
            lvItemDto.setCartId(lvCart.getId());
            lvItemDto.setPrice(PriceUtils.getPriceValue(lvItemPrices, com.flowiee.pms.modules.inventory.enums.PriceType.RTL));
            lvItemDto.setExtraDiscount(lvItem.getExtraDiscount());

            lvItemDTOs.add(lvItemDto);
        }

        return lvItemDTOs;
    }

    @Transactional
    @Override
    public String deleteItem(Long pCartId, Long pItemId) {
        OrderCart lvCart = super.findEntById(pCartId, true);
        //Check something...

        mvCartItemsRepository.deleteById(pItemId);

        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public ItemsDTO updateItemQuantity(Long pCartId, Long pItemId, Integer pQuantity) {
        Optional<Items> lvItem = mvCartItemsRepository.findById(pItemId);
        if (lvItem.isPresent()) {
            ProductDetail lvProductVariant = lvItem.get().getProductDetail();
            if (lvProductVariant.getAvailableSalesQty() == 0 || lvProductVariant.getAvailableSalesQty() < pQuantity) {
                throw new AppException(ErrorCode.ProductOutOfStock, new Object[]{lvProductVariant.getVariantName()}, null, getClass(), null);
            }

            lvItem.get().setQuantity(pQuantity);
            Items lvItemUpdated = mvCartItemsRepository.save(lvItem.get());

            ItemsDTO lvItemDTO = new ItemsDTO();
            lvItemDTO.setCartId(lvItemUpdated.getOrderCart().getId());
            lvItemDTO.setItemId(lvItemUpdated.getId());
            lvItemDTO.setPrice(lvItemUpdated.getPrice());
            lvItemDTO.setQuantity(lvItemUpdated.getQuantity());
            lvItemDTO.setExtraDiscount(lvItemUpdated.getExtraDiscount());
            lvItemDTO.setNote(lvItemUpdated.getNote());
            //(Price * Quantity) - Extra Discount
            BigDecimal lvSubTotal = (lvItemDTO.getPrice().multiply(BigDecimal.valueOf(lvItemUpdated.getQuantity()))).subtract(lvItemDTO.getExtraDiscount());
            lvItemDTO.setSubTotal(lvSubTotal);

            return lvItemDTO;
        }

        throw new BadRequestException();
    }

    @Override
    public BigDecimal getCartValuePreDiscount(Long pCartId) {
        return mvCartItemsRepository.calTotalAmountWithoutDiscount(pCartId);
    }

    @Override
    public void markOrderFinished(Long pCartId) {
        OrderCart lvCart = super.findEntById(pCartId, true);
        lvCart.setIsFinish(true);
        mvEntityRepository.save(lvCart);
    }
}