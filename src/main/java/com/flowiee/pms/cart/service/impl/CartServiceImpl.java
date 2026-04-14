package com.flowiee.pms.cart.service.impl;

import com.flowiee.pms.product.dto.ProductPriceDTO;
import com.flowiee.pms.product.enums.SalesType;
import com.flowiee.pms.product.service.ProductPriceService;
import com.flowiee.pms.shared.base.BaseService;
import com.flowiee.pms.shared.request.BaseParameter;
import com.flowiee.pms.shared.util.CoreUtils;
import com.flowiee.pms.product.entity.ProductDetail;
import com.flowiee.pms.order.dto.OrderCartDTO;
import com.flowiee.pms.cart.entity.Items;
import com.flowiee.pms.cart.entity.OrderCart;
import com.flowiee.pms.shared.exception.AppException;
import com.flowiee.pms.shared.exception.BadRequestException;
import com.flowiee.pms.cart.dto.ItemsDTO;
import com.flowiee.pms.cart.model.CartItemsReq;
import com.flowiee.pms.cart.model.CartReq;
import com.flowiee.pms.product.dto.ProductVariantDTO;
import com.flowiee.pms.cart.repository.CartItemsRepository;
import com.flowiee.pms.cart.repository.OrderCartRepository;
import com.flowiee.pms.product.service.ProductVariantService;

import com.flowiee.pms.cart.service.CartItemsService;
import com.flowiee.pms.cart.service.CartService;
import com.flowiee.pms.shared.enums.*;
import com.flowiee.pms.shared.util.SecurityUtils;
import com.flowiee.pms.system.service.SystemLogService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartServiceImpl extends BaseService<OrderCart, OrderCartDTO, OrderCartRepository> implements CartService {
    CartItemsService mvCartItemsService;
    OrderCartRepository mvCartRepository;
    CartItemsRepository mvCartItemsRepository;
    ProductVariantService mvProductVariantService;
    ProductPriceService mvProductPriceService;
    ModelMapper mvModelMapper;
    SystemLogService systemLogService;

    public CartServiceImpl(OrderCartRepository pCartRepository, CartItemsService pCartItemsService, CartItemsRepository pCartItemsRepository, ProductVariantService pProductVariantService, ProductPriceService pProductPriceService, ModelMapper pModelMapper, SystemLogService pSystemLogService) {
        super(OrderCart.class, OrderCartDTO.class, pCartRepository);
        this.mvCartItemsService = pCartItemsService;
        this.mvCartRepository = pCartRepository;
        this.mvCartItemsRepository = pCartItemsRepository;
        this.mvProductVariantService = pProductVariantService;
        this.mvProductPriceService = pProductPriceService;
        this.mvModelMapper = pModelMapper;
        this.systemLogService = pSystemLogService;
    }

    @Override
    public OrderCartDTO addDraftCart() {
        OrderCartDTO lvCartDto = new OrderCartDTO();
        lvCartDto.setIsFinish(false);
        lvCartDto.setCreatedBy(SecurityUtils.getCurrentUser().getId());
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
                ProductPriceDTO lvPrice = mvProductPriceService.getPrice(item.getProductDetail().getId());
                BigDecimal lvRetailPrice = lvPrice.getRetailPrice();
                BigDecimal lvWholesalePrice = lvPrice.getWholesalePrice();
                SalesType priceType = SalesType.valueOf(item.getPriceType());
                if (priceType.equals(SalesType.L)) {
                    item.setPriceOriginal(lvRetailPrice);
                    item.setPrice(lvRetailPrice);
                }
                if (priceType.equals(SalesType.S)) {
                    item.setPriceOriginal(lvWholesalePrice);
                    item.setPrice(lvWholesalePrice);
                }

                item.getProductDetail().setAvailableSalesQty(item.getProductDetail().getStorageQty() - item.getProductDetail().getDefectiveQty());//Enhance later
            }
        }
        return listCart;
    }

    @Override
    public List<OrderCartDTO>find(BaseParameter pParam) {
        return super.find(pParam);
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
        List<OrderCart> lvCurrentUserCarts = mvCartRepository.findByAccountId(SecurityUtils.getCurrentUser().getId());
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
    public boolean delete(Long cartId) {
        OrderCart cart = super.findEntById(cartId, true);
        mvCartItemsRepository.deleteAllItems(cart.getId());
        mvCartRepository.deleteById(cartId);

        systemLogService.writeLogDelete(MODULE.PRODUCT, ACTION.PRO_CART_C, MasterObject.Cart, "Xóa/Reset giỏ hàng", "cartId = " + cartId);

        return true;
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

            ProductPriceDTO lvPrice = mvProductPriceService.getPrice(lvProductVariant.getId());
            BigDecimal lvRetailPrice = lvPrice.getRetailPrice();
            BigDecimal lvRetailPriceDiscount = lvPrice.getRetailPriceDiscount();

            BigDecimal lvItemPrice = lvRetailPriceDiscount != null ? lvRetailPriceDiscount : lvRetailPrice;
            BigDecimal lvItemOriginalPrice = lvRetailPrice;

            if (mvCartItemsRepository.existsByCartAndProductVariant(lvCartId, lvProductVariant.getId())) {
                Items items = mvCartItemsService.findItemByCartAndProductVariant(lvCartId, lvProductVariant.getId());
                //mvCartItemsService.increaseItemQtyInCart(items.getId(), items.getQuantity() + 1);
                items.setQuantity(items.getQuantity() + lvItemQty);
                items.setPriceType(SalesType.L.name());
                items.setPrice(lvItemPrice);
                items.setPriceOriginal(lvItemOriginalPrice);
                mvCartItemsRepository.save(items);
            } else {
                ItemsDTO itemsDto = mvModelMapper.map(Items.builder()
                        .orderCart(orderCart)
                        .productDetail(new ProductDetail(lvProductVariant.getId()))
                        .priceType(SalesType.L.name())
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

            ProductPriceDTO lvPrice = mvProductPriceService.getPrice(productVariant.getId());
            BigDecimal lvRetailPrice = lvPrice.getRetailPrice();
            BigDecimal lvRetailPriceDiscount = lvPrice.getRetailPriceDiscount();
            BigDecimal lvWholesalePrice = lvPrice.getWholesalePrice();
            BigDecimal lvWholesalePriceDiscount = lvPrice.getWholesalePriceDiscount();

            //String lvPriceType = pItemToUpdate.getPriceType();
            String lvPriceType = SalesType.L.name();

            lvItem.setNote(pItemToUpdate.getNote());
            lvItem.setQuantity(pItemToUpdate.getQuantity());
            if (!lvItem.getPriceType().equals(lvPriceType)) {
                if (lvPriceType.equals(SalesType.L.name())) {
                    lvItem.setPrice(lvRetailPriceDiscount);
                    lvItem.setPriceOriginal(lvRetailPrice);
                    //lvItem.setPriceType(PriceType.L.name());
                }
                if (lvPriceType.equals(SalesType.S.name())) {
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
}