package com.flowiee.pms.order.service.impl;

import com.flowiee.pms.product.dto.ProductPriceDTO;
import com.flowiee.pms.product.entity.ProductDetail;
import com.flowiee.pms.cart.entity.Items;
import com.flowiee.pms.order.entity.Order;
import com.flowiee.pms.order.entity.OrderDetail;
import com.flowiee.pms.product.enums.SalesType;
import com.flowiee.pms.product.service.ProductPriceService;
import com.flowiee.pms.shared.exception.AppException;
import com.flowiee.pms.shared.exception.BadRequestException;
import com.flowiee.pms.shared.exception.EntityNotFoundException;
import com.flowiee.pms.order.dto.OrderDTO;
import com.flowiee.pms.product.dto.ProductVariantDTO;
import com.flowiee.pms.cart.repository.CartItemsRepository;
import com.flowiee.pms.product.service.ProductVariantService;
import com.flowiee.pms.shared.util.ChangeLog;
import com.flowiee.pms.shared.util.CoreUtils;
import com.flowiee.pms.order.repository.OrderDetailRepository;
import com.flowiee.pms.order.service.OrderHistoryService;
import com.flowiee.pms.order.service.OrderItemsService;
import com.flowiee.pms.shared.enums.*;
import com.flowiee.pms.system.service.SystemLogService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderItemsServiceImpl implements OrderItemsService {
    SystemLogService mvSystemLogService;
    OrderHistoryService mvOrderHistoryService;
    OrderDetailRepository mvOrderDetailRepository;
    ProductPriceService mvProductPriceService;
    @Autowired
    @NonFinal
    @Lazy
    ProductVariantService mvProductVariantService;
    CartItemsRepository   mvCartItemsRepository;

    @Override
    public OrderDetail findById(Long orderDetailId, boolean pThrowException) {
        Optional<OrderDetail> entityOptional = mvOrderDetailRepository.findById(orderDetailId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"cart item"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public List<OrderDetail> save(OrderDTO pOrder, List<String> productVariantIds) {
        List<OrderDetail> itemAdded = new ArrayList<>();
        for (String productVariantId : productVariantIds) {
            ProductVariantDTO productDetail = mvProductVariantService.findById(Long.parseLong(productVariantId), false);
            if (productDetail != null) {
                OrderDetail orderDetail = mvOrderDetailRepository.findByOrderIdAndProductVariantId(pOrder.getId(), productDetail.getId());
                if (orderDetail != null) {
                    orderDetail.setQuantity(orderDetail.getQuantity() + 1);
                    itemAdded.add(mvOrderDetailRepository.save(orderDetail));
                } else {
                    ProductPriceDTO lvPrice = mvProductPriceService.getPrice(productDetail.getId());
                    itemAdded.add(this.save(OrderDetail.builder()
                            .order(new Order(pOrder.getId()))
                            .productDetail(new ProductDetail(productDetail.getId()))
                            .quantity(1)
                            .status(true)
                            //.price(itemPrice.getAppliedValue())
                            .price(lvPrice.getRetailPrice())
                            //.priceOriginal(itemPrice.getPriceValue())
                            .priceOriginal(lvPrice.getRetailPrice())
                            .extraDiscount(BigDecimal.ZERO)
                            .priceType(SalesType.L.name())
                            .build()));
                }
            }
        }
        return itemAdded;
    }

    @Override
    public List<OrderDetail> save(Long pCartId, Long pOrderId) {
        List<OrderDetail> lvOrderDetailList = new ArrayList<>();

        List<Items> lvItemsList = mvCartItemsRepository.findByCartId(pCartId);
        if (lvItemsList == null || lvItemsList.isEmpty()) {
            return lvOrderDetailList;
        }

        for (Items items : lvItemsList) {
            Long lvProductVariantId = items.getProductDetail().getId();
            ProductVariantDTO productDetail = mvProductVariantService.findById(lvProductVariantId, true);
            String productVariantName = productDetail.getVariantName();
            int lvItemQuantity = mvCartItemsRepository.findQuantityByProductVariantId(pCartId, lvProductVariantId);
            if (lvItemQuantity <= 0) {
                throw new BadRequestException(String.format("The quantity of product %s must greater than zero!", productVariantName));
            }
            if (lvItemQuantity > productDetail.getAvailableSalesQty()) {
                throw new AppException(ErrorCode.ProductOutOfStock, new Object[]{productVariantName}, null, getClass(), null);
            }
            lvOrderDetailList.add(save(OrderDetail.builder()
                    .order(new Order(pOrderId))
                    .productDetail(new ProductDetail(productDetail.getId()))
                    .quantity(lvItemQuantity)
                    .status(true)
                    .note(items.getNote())
                    .price(items.getPrice())
                    .priceOriginal(items.getPriceOriginal())
                    .extraDiscount(CoreUtils.coalesce(items.getExtraDiscount()))
                    .priceType(items.getPriceType())
                    .build()));
        }
        return lvOrderDetailList;
    }

    @Override
    public OrderDetail save(OrderDetail orderDetail) {
        orderDetail.setExtraDiscount(CoreUtils.coalesce(orderDetail.getExtraDiscount(), BigDecimal.ZERO));
        try {
            OrderDetail orderDetailSaved = mvOrderDetailRepository.save(orderDetail);
            mvSystemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_ORD_C, MasterObject.OrderDetail, "Thêm mới item vào đơn hàng", orderDetail.toString());
            log.info("{}: Thêm mới item vào đơn hàng {}", OrderServiceImpl.class.getName(), orderDetail.toString());
            return orderDetailSaved;
        } catch (RuntimeException ex) {
            throw new AppException(ex);
        }
    }

    @Override
    public OrderDetail update(OrderDetail orderDetail, Long orderDetailId) {
        try {
            OrderDetail orderDetailOpt = this.findById(orderDetailId, true);

            ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(orderDetailOpt));

            int lvQuantity = orderDetail.getQuantity();
            BigDecimal lvExtraDiscount = orderDetail.getExtraDiscount();
            String lvNote = orderDetail.getNote();

            orderDetailOpt.setQuantity(lvQuantity);
            orderDetailOpt.setExtraDiscount(lvExtraDiscount);
            orderDetailOpt.setNote(lvNote);
            OrderDetail orderItemUpdated = mvOrderDetailRepository.save(orderDetailOpt);

            changeLog.setNewObject(orderItemUpdated);
            changeLog.doAudit();

            String logTitle = "Cập nhật đơn hàng " + orderItemUpdated.getOrder().getCode();

            mvOrderHistoryService.save(changeLog.getLogChanges(), logTitle, orderDetailId, orderDetailId);
            mvSystemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.PRO_ORD_U, MasterObject.OrderDetail, logTitle, changeLog);
            log.info("{}: Cập nhật item of đơn hàng {}", OrderServiceImpl.class.getName(), orderItemUpdated.toString());

            return orderItemUpdated;
        } catch (RuntimeException ex) {
            throw new AppException(ex);
        }
    }

    @Override
    public boolean delete(Long orderDetailId) {
        OrderDetail orderDetail = this.findById(orderDetailId, true);
        try {
            mvOrderDetailRepository.deleteById(orderDetailId);
            mvSystemLogService.writeLogDelete(MODULE.PRODUCT, ACTION.PRO_ORD_D, MasterObject.OrderDetail, "Xóa item of đơn hàng", orderDetail.toString());
            log.info("{}: Xóa item of đơn hàng {}", OrderServiceImpl.class.getName(), orderDetail.toString());
            return true;
        } catch (RuntimeException ex) {
            throw new AppException(ex);
        }
    }
}