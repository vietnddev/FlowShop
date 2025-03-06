package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.entity.product.ProductPrice;
import com.flowiee.pms.entity.sales.Items;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.entity.sales.OrderDetail;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.model.dto.ProductVariantDTO;
import com.flowiee.pms.repository.product.ProductPriceRepository;
import com.flowiee.pms.repository.sales.CartItemsRepository;
import com.flowiee.pms.service.product.ProductVariantService;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.repository.sales.OrderDetailRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.sales.OrderHistoryService;
import com.flowiee.pms.service.sales.OrderItemsService;
import com.flowiee.pms.service.system.SystemLogService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderItemsServiceImpl extends BaseService implements OrderItemsService {
    SystemLogService       mvSystemLogService;
    OrderHistoryService    mvOrderHistoryService;
    OrderDetailRepository  mvOrderDetailRepository;
    ProductPriceRepository mvProductPriceRepository;
    @Autowired
    @NonFinal
    @Lazy
    ProductVariantService mvProductVariantService;
    CartItemsRepository   mvCartItemsRepository;

    @Override
    public List<OrderDetail> findAll() {
        return mvOrderDetailRepository.findAll();
    }

    @Override
    public OrderDetail findById(Long orderDetailId, boolean pThrowException) {
        Optional<OrderDetail> entityOptional = mvOrderDetailRepository.findById(orderDetailId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"cart item"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return mvOrderDetailRepository.findByOrderId(orderId);
    }

    @Override
    public List<OrderDetail> save(Order pOrder, List<String> productVariantIds) {
        List<OrderDetail> itemAdded = new ArrayList<>();
        for (String productVariantId : productVariantIds) {
            ProductVariantDTO productDetail = mvProductVariantService.findById(Long.parseLong(productVariantId), false);
            if (productDetail != null) {
                OrderDetail orderDetail = mvOrderDetailRepository.findByOrderIdAndProductVariantId(pOrder.getId(), productDetail.getId());
                if (orderDetail != null) {
                    orderDetail.setQuantity(orderDetail.getQuantity() + 1);
                    itemAdded.add(mvOrderDetailRepository.save(orderDetail));
                } else {
                    ProductPrice itemPrice = mvProductPriceRepository.findPricePresent(null, productDetail.getId());
                    if (itemPrice == null) {
                        throw new AppException(String.format("Sản phẩm %s chưa được thiết lập giá bán!", productDetail.getVariantName()));
                    }
                    itemAdded.add(this.save(OrderDetail.builder()
                            .order(new Order(pOrder.getId()))
                            .productDetail(productDetail)
                            .quantity(1)
                            .status(true)
                            .price(itemPrice.getRetailPriceDiscount())
                            .priceOriginal(itemPrice.getRetailPrice())
                            .extraDiscount(BigDecimal.ZERO)
                            .priceType(PriceType.L.name())
                            .build()));
                }
            }
        }
        return itemAdded;
    }

    @Override
    public List<OrderDetail> save(Long pCartId, Long pOrderId, List<Items> pItemsList) {
        List<OrderDetail> lvOrderDetailList = new ArrayList<>();
        if (pItemsList == null || pItemsList.isEmpty()) {
            return lvOrderDetailList;
        }
        for (Items items : pItemsList) {
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
                    .productDetail(productDetail)
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
            logger.info("{}: Thêm mới item vào đơn hàng {}", OrderServiceImpl.class.getName(), orderDetail.toString());
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
            logger.info("{}: Cập nhật item of đơn hàng {}", OrderServiceImpl.class.getName(), orderItemUpdated.toString());

            return orderItemUpdated;
        } catch (RuntimeException ex) {
            throw new AppException(ex);
        }
    }

    @Override
    public String delete(Long orderDetailId) {
        OrderDetail orderDetail = this.findById(orderDetailId, true);
        try {
            mvOrderDetailRepository.deleteById(orderDetailId);
            mvSystemLogService.writeLogDelete(MODULE.PRODUCT, ACTION.PRO_ORD_D, MasterObject.OrderDetail, "Xóa item of đơn hàng", orderDetail.toString());
            logger.info("{}: Xóa item of đơn hàng {}", OrderServiceImpl.class.getName(), orderDetail.toString());
            return MessageCode.DELETE_SUCCESS.getDescription();
        } catch (RuntimeException ex) {
            throw new AppException(ex);
        }
    }
}