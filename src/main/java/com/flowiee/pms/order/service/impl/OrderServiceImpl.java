package com.flowiee.pms.order.service.impl;

import com.flowiee.pms.cart.repository.OrderCartRepository;
import com.flowiee.pms.customer.entity.Customer;
import com.flowiee.pms.order.entity.*;
import com.flowiee.pms.order.enums.OrderStatus;
import com.flowiee.pms.order.mapper.OrderConvert;
import com.flowiee.pms.order.model.*;
import com.flowiee.pms.order.repository.OrderDetailRepository;
import com.flowiee.pms.order.repository.OrderRepository;
import com.flowiee.pms.order.repository.OrderReturnItemRepository;
import com.flowiee.pms.order.repository.OrderReturnRepository;
import com.flowiee.pms.order.service.*;
import com.flowiee.pms.promotion.entity.LoyaltyTransaction;
import com.flowiee.pms.promotion.service.LoyaltyProgramService;
import com.flowiee.pms.promotion.service.VoucherTicketService;
import com.flowiee.pms.shared.base.BaseService;
import com.flowiee.pms.product.entity.ProductDetail;
import com.flowiee.pms.inventory.entity.TransactionGoods;
import com.flowiee.pms.product.service.ProductVariantService;
import com.flowiee.pms.inventory.service.TransactionGoodsService;
import com.flowiee.pms.media.entity.FileStorage;
import com.flowiee.pms.order.dto.OrderReturnDTO;
import com.flowiee.pms.order.dto.OrderReturnItemDTO;
import com.flowiee.pms.order.enums.OrderRefundMethod;
import com.flowiee.pms.order.enums.OrderReturnCondition;
import com.flowiee.pms.order.enums.OrderReturnStatus;
import com.flowiee.pms.shared.base.FlwSys;
import com.flowiee.pms.shared.enums.*;
import com.flowiee.pms.shared.util.*;
import com.flowiee.pms.system.entity.Account;
import com.flowiee.pms.system.enums.ConfigCode;
import com.flowiee.pms.shared.exception.AppException;
import com.flowiee.pms.shared.exception.BadRequestException;
import com.flowiee.pms.system.enums.SerialCode;
import com.flowiee.pms.system.service.SendCustomerNotificationService;
import com.flowiee.pms.order.dto.OrderDTO;
import com.flowiee.pms.shared.exception.DataInUseException;
import com.flowiee.pms.system.entity.Category;

import com.flowiee.pms.system.service.SerialService;
import com.flowiee.pms.system.service.SystemLogService;
import com.google.zxing.WriterException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl extends BaseService<Order, OrderDTO, OrderRepository> implements OrderService {
    private final SendCustomerNotificationService mvSendCustomerNotificationService;
    private final OrderGenerateQRCodeService mvOrderGenerateQRCodeService;
    private final OrderReturnItemRepository mvOrderReturnItemRepository;
    private final TransactionGoodsService mvTransactionGoodsService;
    private final OrderReturnRepository mvOrderReturnRepository;
    private final ProductVariantService mvProductVariantService;
    private final LoyaltyProgramService mvLoyaltyProgramService;
    private final OrderDetailRepository mvOrderDetailRepository;
    private final VoucherTicketService mvVoucherTicketService;
    private final OrderHistoryService mvOrderHistoryService;
    private final OrderValidatorService mvOrderValidator;
    private final OrderItemsService mvOrderItemsService;
    private final OrderCartRepository mvCartRepository;
    private final SystemLogService mvSystemLogService;
    private final SerialService mvSerialService;
    private final ModelMapper mvModelMapper;

    public OrderServiceImpl(OrderRepository pOrderRepository, SendCustomerNotificationService pSendCustomerNotificationService, OrderItemsService pOrderItemsService, OrderGenerateQRCodeService pOrderGenerateQRCodeService, OrderHistoryService pOrderHistoryService, VoucherTicketService pVoucherTicketService, LoyaltyProgramService pLoyaltyProgramService, SystemLogService pSystemLogService, OrderReturnRepository pOrderReturnRepository, OrderReturnItemRepository pOrderReturnItemRepository, ProductVariantService pProductVariantService, ModelMapper pModelMapper, TransactionGoodsService pTransactionGoodsService, OrderValidatorService pOrderValidatorService, OrderCartRepository pCartRepository, OrderDetailRepository pOrderDetailRepository, SerialService pSerialService) {
        super(Order.class, OrderDTO.class, pOrderRepository);
        this.mvSendCustomerNotificationService = pSendCustomerNotificationService;
        this.mvOrderGenerateQRCodeService = pOrderGenerateQRCodeService;
        this.mvOrderReturnItemRepository = pOrderReturnItemRepository;
        this.mvTransactionGoodsService = pTransactionGoodsService;
        this.mvProductVariantService = pProductVariantService;
        this.mvLoyaltyProgramService = pLoyaltyProgramService;
        this.mvOrderDetailRepository = pOrderDetailRepository;
        this.mvOrderReturnRepository = pOrderReturnRepository;
        this.mvVoucherTicketService = pVoucherTicketService;
        this.mvOrderHistoryService = pOrderHistoryService;
        this.mvOrderValidator = pOrderValidatorService;
        this.mvOrderItemsService = pOrderItemsService;
        this.mvSystemLogService = pSystemLogService;
        this.mvCartRepository = pCartRepository;
        this.mvSerialService = pSerialService;
        this.mvModelMapper = pModelMapper;
    }

    private final BigDecimal mvDefaultShippingCost = BigDecimal.ZERO;
    private final BigDecimal mvDefaultPackagingCost = BigDecimal.ZERO;
    private final BigDecimal mvDefaultGiftWrapCost = BigDecimal.ZERO;
    private final BigDecimal mvDefaultCodFee = BigDecimal.ZERO;

    private static final int GENERATE_TRACKING_CODE_MAX_RETRIES = 30;
    private static final String PREFIX_RETURNS_ORDER_CODE = "TH";

    @Override
    public Page<OrderDTO> find(OrderReq pOrderReq) {
        Pageable lvPageable = getPageable(pOrderReq.getPageNum(), pOrderReq.getPageSize(), Sort.by("orderTime").descending());
        LocalDateTime lvOrderTimeFrom = DateTimeUtil.getFilterStartTime(pOrderReq.getFromDate());
        LocalDateTime lvOrderTimeTo = DateTimeUtil.getFilterEndTime(pOrderReq.getToDate());
        if (!CoreUtils.isNullStr(pOrderReq.getDateFilter())) {
            LocalDateTime[] lvFromDateToDate = DateTimeUtil.getFromDateToDate(pOrderReq.getFromDate(), pOrderReq.getToDate(), pOrderReq.getDateFilter());
            lvOrderTimeFrom = lvFromDateToDate[0];
            lvOrderTimeTo = lvFromDateToDate[1];
        }

        QueryBuilder<Order> lvQueryBuilder = createQueryBuilder(Order.class)
                .addEqual("id", pOrderReq.getOrderId())
                .addEqual("paymentMethod.id", pOrderReq.getPaymentMethodId())
                .addEqual("orderStatus", pOrderReq.getOrderStatus())
                .addEqual("salesChannel.id", pOrderReq.getSalesChannelId())
                .addEqual("customer.id", pOrderReq.getCustomerId())
                .addEqual("nhanVienBanHang.id", pOrderReq.getSellerId())
                .addEqual("nhanVienBanHang.branch.id", pOrderReq.getBranchId())
                .addEqual("customer.groupCustomer.id", pOrderReq.getGroupCustomerId())
                .addLike(pOrderReq.getTxtSearch(), "receiverName", "receiverPhone", "code")
                .addBetween("orderTime", lvOrderTimeFrom, lvOrderTimeTo);
        List<Order> lvResultList = lvQueryBuilder.build(lvPageable).getResultList();
        long total = lvQueryBuilder.buildCount();

        List<OrderDTO> lvDTOs = new ArrayList<>();
        for (Order lvOrder : lvResultList) {
            OrderDTO lvDto = OrderConvert.toDto(lvOrder);
            lvDto.setTotalAmount(OrderUtils.calAmount(lvOrder));

            lvDTOs.add(lvDto);
        }

        return new PageImpl<>(lvDTOs, lvPageable, total);
    }

    @Override
    public OrderDTO findDtoById(Long pOrderId, boolean pThrowException) {
        Order lvOrderEnt = super.findEntById(pOrderId, pThrowException);
        OrderDTO lvOrderDto = OrderConvert.toDto(lvOrderEnt);

        if (!CollectionUtils.isEmpty(lvOrderEnt.getListImageQR())) {
            FileStorage imageQRCode = lvOrderEnt.getListImageQR().get(0);
            lvOrderDto.setQrCode(FileUtils.getImageUrl(imageQRCode, false));
        }

        return lvOrderDto;
    }

    @Override
    public OrderDTO findByTrackingCode(String pTrackingCode) {
        return convertDTO(mvEntityRepository.findByTrackingCode(pTrackingCode));
    }

    @Transactional
    @Override
    public OrderDTO createOrder(CreateOrderReq pRequest) {
        Long lvCartId = pRequest.getCartId();
        BigDecimal lvAmountDiscount = pRequest.getAmountDiscount();
        Long lvCustomerId = pRequest.getCustomerId();
        String lvCouponCode = pRequest.getCouponCode();
        LocalDateTime lvOrderTime = getOrderTime(pRequest.getOrderTime());
        Long lvSellerId = pRequest.getSalesAssistantId();

        //1. Validate
        mvOrderValidator.validateCreateOrder(pRequest);
        log.info("Create order - Done step 1");

        //2. Create order entity
        Order order = Order.builder()
                .code(getNextOrderCode())
                .customer(new Customer(lvCustomerId))
                .salesChannel(new Category(pRequest.getSalesChannelId()))
                .seller(new Account(lvSellerId))
                .note(pRequest.getNote())
                .customerNote(pRequest.getCustomerNote())
                .orderTime(lvOrderTime != null ? lvOrderTime : LocalDateTime.now())
                .receiverName(pRequest.getRecipientName())
                .receiverPhone(pRequest.getRecipientPhone())
                .receiverEmail(pRequest.getRecipientEmail())
                .receiverAddress(pRequest.getShippingAddress())
                .paymentMethod(new Category(pRequest.getPaymentMethodId()))
                .paymentStatus(false)
                .couponCode(ObjectUtils.isNotEmpty(lvCouponCode) ? lvCouponCode : null)
                .amountDiscount(CoreUtils.coalesce(lvAmountDiscount))
                .packagingCost(CoreUtils.coalesce(pRequest.getPackagingCost(), mvDefaultPackagingCost))
                .shippingCost(CoreUtils.coalesce(pRequest.getShippingCost(), mvDefaultShippingCost))
                .deliveryMethod(new Category(pRequest.getDeliveryMethodId()))
                .deliveredBy("STORE_DELIVERY")
                .giftWrapCost(CoreUtils.coalesce(pRequest.getGiftWrapCost(), mvDefaultGiftWrapCost))
                .codFee(CoreUtils.coalesce(pRequest.getCodFee(), mvDefaultCodFee))
                .isGiftWrapped(false)
                .orderStatus(OrderStatus.PROCESSING)
                .trackingCode(generateTrackingCode())
                .build();
        log.info("Create order - Done step 2");

        //3. Save order
        Order lvOrderSaved = mvEntityRepository.save(order);
        log.info("Create order - Done step 3");

        //4. Save order items
        List<OrderDetail> lvOrderItemsList = mvOrderItemsService.save(lvCartId, lvOrderSaved.getId());
        lvOrderSaved.setListOrderDetail(lvOrderItemsList);
        log.info("Create order - Done step 4");

        //5. Process voucher
        mvVoucherTicketService.markCouponAsUsed(order.getCouponCode(), lvCustomerId);
        log.info("Create order - Done step 5");

        //6. Process loyalty points
        if (Boolean.TRUE.equals(pRequest.getAccumulateBonusPoints())) {
            LoyaltyTransaction lvLoyaltyTransaction = mvLoyaltyProgramService.accumulatePoints(lvOrderSaved, null);
            mvEntityRepository.updateLoyaltyTransaction(lvOrderSaved.getId(), lvLoyaltyTransaction);
        }
        log.info("Create order - Done step 6");

        //7. Generate QR code (async)
        try {
            mvOrderGenerateQRCodeService.generateOrderQRCode(lvOrderSaved.getId());
        } catch (IOException | WriterException e ) {
            log.error(String.format("Can't generate QR Code for Order %s", lvOrderSaved.getCode()), e);
        }
        log.info("Create order - Done step 7");

        //8. Clear cart
        mvCartRepository.updateIsFinish(lvCartId, true);
        log.info("Create order - Done step 8");

        // 9. Log and notification
        mvSystemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_ORD_C, MasterObject.Order, "Thêm mới đơn hàng", lvOrderSaved.getCode());
        log.info("Insert new order success! insertBy={}", SecurityUtils.getCurrentUser().getUsername());
        log.info("Create order - All done");

        return OrderConvert.toDto(lvOrderSaved);
    }

    private LocalDateTime getOrderTime(String pOrderTime) {
        if (pOrderTime == null) {
            return LocalDateTime.now();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");
        DateTimeFormatter formatterUS = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

        try {
            return LocalDateTime.parse(pOrderTime, formatter);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            try {
                TemporalAccessor temporalAccessor = formatterUS.parse(pOrderTime);
                return LocalDateTime.from(temporalAccessor);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    @Transactional
    @Override
    public OrderDTO updateOrder(UpdateOrderReq pRequest, Long pOrderId) {
        // 1. Get and validate order exists
        Order lvCurrentOrder = mvEntityRepository.findById(pOrderId).orElseThrow(() -> new AppException("Order not found!"));

        //2. Validate request
        mvOrderValidator.validateUpdateOrder(pRequest, pOrderId);

        //3. Prepare change log
        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(lvCurrentOrder));

        //4. Save new field's value
        if (pRequest.getRecipientName() != null) {
            lvCurrentOrder.setReceiverName(pRequest.getRecipientName());
        }
        if (pRequest.getRecipientPhone() != null) {
            lvCurrentOrder.setReceiverPhone(pRequest.getRecipientPhone());
        }
        if (pRequest.getRecipientEmail() != null) {
            lvCurrentOrder.setReceiverEmail(pRequest.getRecipientEmail());
        }
        if (pRequest.getShippingAddress() != null) {
            lvCurrentOrder.setReceiverAddress(pRequest.getShippingAddress());
        }
        if (pRequest.getNote() != null) {
            lvCurrentOrder.setNote(pRequest.getNote());
        }

        Order lvUpdatedOrder = mvEntityRepository.save(lvCurrentOrder);

        //Log changes
        changeLog.setNewObject(lvUpdatedOrder);
        changeLog.doAudit();
        mvOrderHistoryService.save(changeLog.getLogChanges(), "Cập nhật đơn hàng", pOrderId, null);
        mvSystemLogService.writeLogUpdate(MODULE.SALES, ACTION.PRO_ORD_U, MasterObject.Order, "Cập nhật đơn hàng", changeLog);
        log.info("Cập nhật đơn hàng {}", lvUpdatedOrder.toString());
        log.info("Update order - All done");

        return OrderConvert.toDto(lvUpdatedOrder);
    }

    @Transactional
    @Override
    public String deleteOrder(Long id) {
        Order lvOrder = super.findEntById(id, true);
        if (Boolean.TRUE.equals(lvOrder.getPaymentStatus())) {
            throw new DataInUseException(ErrorCode.ERROR_DATA_LOCKED.getDescription());
        }

        mvEntityRepository.deleteById(id);

        mvSystemLogService.writeLogDelete(MODULE.PRODUCT, ACTION.PRO_ORD_D, MasterObject.Order, "Xóa đơn hàng", lvOrder.toString());
        log.info("Đã xóa đơn hàng với ID={}", id);

        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public List<OrderReturnDTO> findReturnedOrders() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<OrderReturn> lvOrders = mvOrderReturnRepository.findAll(pageable).getContent();
        return lvOrders.stream()
                .map(i -> {
                    Order lvOrderOriginal = i.getOrder();
                    OrderReturnDTO lvDto = mvModelMapper.map(i, OrderReturnDTO.class);
                    lvDto.setOrderId(lvOrderOriginal.getId());
                    lvDto.setOrderCode(lvOrderOriginal.getCode());
                    lvDto.setSeller(lvOrderOriginal.getSeller().getFullName());
                    lvDto.setCustomerName(lvOrderOriginal.getCustomer().getCustomerName());
                    lvDto.setOriginalItemQty(lvOrderOriginal.getListOrderDetail().size());
                    if (CollectionUtils.isEmpty(i.getOrderReturnItemList())) {
                        lvDto.setItems(new ArrayList<>());
                    } else {
                        lvDto.setItems(i.getOrderReturnItemList().stream()
                                .map(j -> OrderReturnItemDTO.builder()
                                        .orderReturnId(j.getId())
                                        .quantity(j.getQuantity())
                                        .itemId(j.getItemId())
                                        .unitPrice(j.getUnitPrice())
                                        .reason(j.getReason())
                                        .condition(j.getCondition())
                                        .build())
                                .toList());
                    }
                    return lvDto;
                })
                .toList();
    }

    @Transactional
    @Override
    public void changeStatus(Long pOrderId, ChangeOrderStatusReq pRequest) {
        Order lvOrder = super.findEntById(pOrderId, true);
        OrderStatus oldStatus = lvOrder.getOrderStatus();
        OrderStatus newStatus = pRequest.getOrderStatus();

        if (oldStatus.equals(newStatus)) {
            return;
        }

        mvOrderValidator.validateStatusTransition(lvOrder, oldStatus, newStatus, pRequest);

        switch (newStatus) {
            case CANCELLED:
                executeCancelOrder(lvOrder, pRequest);
            case REFUNDED:
                executeRefundOrder(lvOrder, pRequest);
            case COMPLETED:
                executeCompleteOrder(lvOrder, pRequest);
            case PROCESSING:
                executeProcessingOrder(lvOrder, pRequest);
            default:
                throw new BadRequestException("Unsupported status change: " + newStatus);
        }
    }

    private void executeCancelOrder(Order pOrder, ChangeOrderStatusReq pRequest) {
        pOrder.setOrderStatus(OrderStatus.CANCELLED);
        pOrder.setCancellationDate(LocalDateTime.now());
        pOrder.setCancellationReason(pRequest.getReason());
        pOrder.setNote(pRequest.getNote());
        mvEntityRepository.save(pOrder);
    }

    private void executeRefundOrder(Order pOrder, ChangeOrderStatusReq pRequest) {
        Order lvOrder = super.findEntById(pRequest.getOrderId(), true);
        List<OrderDetail> lvOriginalItems = lvOrder.getListOrderDetail();
        boolean isReturnAllItems = false;
        OrderRefundMethod refundMethod =  pRequest.getRefundMethod();

        //Create record for returning
        OrderReturn lvReturnsRecord = mvOrderReturnRepository.save(OrderReturn.builder()
                .order(lvOrder)
                .returnsCode(mvSerialService.getNextSerial(SerialCode.OrderReturn))
                .reason(pRequest.getReason())
                .returnDate(LocalDateTime.now())
                .refundMethod(refundMethod != null ? refundMethod : OrderRefundMethod.BANKING)
                .refundAmount(CoreUtils.coalesce(pRequest.getRefundAmount()))
                .isRefunded(pRequest.getIsRefunded() != null ? pRequest.getIsRefunded() : false)
                .status(pRequest.getReturnStatus() != null ? pRequest.getReturnStatus() : OrderReturnStatus.PENDING)
                .build());

        //Create items for the record above
        List<OrderReturnItem> lvRecordedItems = new ArrayList<>();
        if (!CollectionUtils.isEmpty(pRequest.getReturnItems())) {
            lvRecordedItems.addAll(mvOrderReturnItemRepository.saveAll(pRequest.getReturnItems().stream()
                    .map(i -> OrderReturnItem.builder()
                            .orderReturn(lvReturnsRecord)
                            .itemId(i.getItemId())
                            .quantity(i.getQuantity())
                            .unitPrice(i.getUnitPrice())
                            .reason(i.getReason())
                            .condition(i.getCondition())
                            .build())
                    .toList()));

            if (!CollectionUtils.isEmpty(lvRecordedItems)) {
                for (OrderReturnItem lvItem : lvRecordedItems) {
                    ProductDetail lvProductInfoItem = mvProductVariantService.findEntById(lvItem.getItemId(), false);
                    if (lvProductInfoItem != null) {
                        mvProductVariantService.updateStockQuantity(lvProductInfoItem.getId(), lvItem.getQuantity(), "I");
                        if (OrderReturnCondition.DAMAGED.equals(lvItem.getCondition())) {
                            mvProductVariantService.updateDefectiveQuantity(lvProductInfoItem.getId(), lvItem.getQuantity(), "I");
                        }
                        //Update the item's status in original order
                        mvOrderDetailRepository.updateIsReturnedStatus(lvItem.getItemId(), true);
                    }
                }

                if (lvRecordedItems.size() == lvOriginalItems.size()) {
                    isReturnAllItems = true;
                }
            }
        }

        //Reverse accumulated point
        LoyaltyTransaction lvLoyaltyTransaction = lvOrder.getLoyaltyTransaction();
        if (lvLoyaltyTransaction != null) {
            mvLoyaltyProgramService.revokePoints(lvOrder);
        }

        //Refund and update into ledger transaction
        {
            //Validate refunding is allowed?
        }

        //Put items back into storage
        for (TransactionGoods lvTransactionGoods : pOrder.getTransactionGoodsList()) {
            Long lvStorageId = lvTransactionGoods.getWarehouse().getId();
            mvTransactionGoodsService.restockReturnedItems(lvStorageId, pOrder.getCode());
        }

        //Save attached images
        {

        }

        //Save return/refund status
        pOrder.setOrderStatus(OrderStatus.REFUNDED);
        pOrder.setRefundAmount(pRequest.getRefundAmount());
        pOrder.setRefundStatus("PROCESSING");
        pOrder.setNote(pRequest.getNote());

        mvEntityRepository.save(pOrder);

        //Log
        StringBuilder lvMessageLog = new StringBuilder("Items id: ");
        String itemIds = lvRecordedItems.stream()
                .map(item -> item.getItemId().toString())
                .collect(Collectors.joining(", "));
        lvMessageLog.append(itemIds);
        mvSystemLogService.writeLogUpdate(MODULE.SALES, ACTION.PRO_ORD_U, MasterObject.Order,
                "Trả đơn hàng " + lvOrder.getCode(),
                lvMessageLog.toString(),
                null);
    }

    private void executeCompleteOrder(Order pOrder, ChangeOrderStatusReq pRequest) {
        pOrder.setOrderStatus(OrderStatus.COMPLETED);
        pOrder.setDeliverySuccessTime(pRequest.getDeliverySuccessTime() != null ?
                pRequest.getDeliverySuccessTime() : LocalDateTime.now());
        mvEntityRepository.save(pOrder);

        mvLoyaltyProgramService.accumulatePoints(pOrder, null);
    }

    private void executeProcessingOrder(Order pOrder, ChangeOrderStatusReq pRequest) {
        pOrder.setOrderStatus(OrderStatus.PROCESSING);
        pOrder.setConfirmedTime(LocalDateTime.now());
        pOrder.setConfirmedBy(SecurityUtils.getCurrentUser().getUsername());
        Order lvUpdatedOrder = mvEntityRepository.save(pOrder);

        if (SysConfigUtils.isYesOption(FlwSys.getSystemConfigs().get(ConfigCode.sendNotifyCustomerOnOrderConfirmation))) {
            mvSendCustomerNotificationService.notifyOrderConfirmation(lvUpdatedOrder, lvUpdatedOrder.getReceiverEmail());
        }
    }


    private String getNextOrderCode() {
        int orderTodayQty = 0;
        List<Order> ordersToday = mvEntityRepository.findByOrderTime(LocalDate.now().atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX));
        if (ordersToday != null) {
            orderTodayQty = ordersToday.size();
        }
        LocalDate currentDate = LocalDate.now();
        String year = String.valueOf(currentDate.getYear()).substring(2);
        String month = String.format("%02d", currentDate.getMonthValue());
        String day = String.format("%02d", currentDate.getDayOfMonth());
        return year + month + day + String.format("%03d", orderTodayQty + 1);
    }

    private String generateTrackingCode() {
        for (int i = 0; i < GENERATE_TRACKING_CODE_MAX_RETRIES; i++) {
            String code = UUID.randomUUID().toString();
            if (!mvEntityRepository.existsByTrackingCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Unable to generate a unique tracking code after " + GENERATE_TRACKING_CODE_MAX_RETRIES + " attempts");
    }
}