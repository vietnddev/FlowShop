package com.flowiee.pms.modules.sales.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.utils.*;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.inventory.entity.TransactionGoods;
import com.flowiee.pms.modules.inventory.service.ProductVariantService;
import com.flowiee.pms.modules.inventory.service.TransactionGoodsService;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.modules.sales.dto.OrderReturnDTO;
import com.flowiee.pms.modules.sales.dto.OrderReturnItemDTO;
import com.flowiee.pms.modules.sales.entity.*;
import com.flowiee.pms.modules.sales.model.OrderReq;
import com.flowiee.pms.modules.sales.model.OrderReturnReq;
import com.flowiee.pms.modules.sales.repository.*;
import com.flowiee.pms.modules.sales.service.*;
import com.flowiee.pms.modules.sales.utils.OrderRefundMethod;
import com.flowiee.pms.modules.sales.utils.OrderReturnCondition;
import com.flowiee.pms.modules.sales.utils.OrderReturnStatus;
import com.flowiee.pms.modules.staff.entity.Account;
import com.flowiee.pms.modules.staff.repository.AccountRepository;
import com.flowiee.pms.modules.system.entity.SystemConfig;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.sales.dto.OrderDetailDTO;
import com.flowiee.pms.modules.sales.model.CreateOrderReq;
import com.flowiee.pms.modules.sales.model.UpdateOrderReq;
import com.flowiee.pms.modules.system.repository.ConfigRepository;
import com.flowiee.pms.modules.system.service.CategoryService;
import com.flowiee.pms.modules.system.service.SendCustomerNotificationService;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.modules.sales.dto.OrderDTO;
import com.flowiee.pms.common.exception.DataInUseException;
import com.flowiee.pms.modules.system.entity.Category;

import com.flowiee.pms.modules.system.service.SystemLogService;
import com.google.zxing.WriterException;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends BaseService<Order, OrderDTO, OrderRepository> implements OrderService {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final SendCustomerNotificationService mvSendCustomerNotificationService;
    private final OrderGenerateQRCodeService mvOrderGenerateQRCodeService;
    private final OrderReturnItemRepository mvOrderReturnItemRepository;
    private final TransactionGoodsService mvTransactionGoodsService;
    private final OrderReturnRepository mvOrderReturnRepository;
    private final ProductVariantService mvProductVariantService;
    private final LoyaltyProgramService mvLoyaltyProgramService;
    private final VoucherTicketService mvVoucherTicketService;
    private final OrderHistoryService mvOrderHistoryService;
    private final OrderItemsService mvOrderItemsService;
    private final AccountRepository mvAccountRepository;
    private final ConfigRepository mvConfigRepository;
    private final SystemLogService mvSystemLogService;
    private final CategoryService mvCategoryService;
    private final CustomerService mvCustomerService;
    private final CartService mvCartService;
    private final ModelMapper mvModelMapper;

    public OrderServiceImpl(OrderRepository pOrderRepository, SendCustomerNotificationService pSendCustomerNotificationService, CartService pCartService, ConfigRepository pConfigRepository, OrderItemsService pOrderItemsService, OrderGenerateQRCodeService pOrderGenerateQRCodeService, OrderHistoryService pOrderHistoryService, VoucherTicketService pVoucherTicketService, LoyaltyProgramService pLoyaltyProgramService, CategoryService pCategoryService, CustomerService pCustomerService, AccountRepository pAccountRepository, SystemLogService pSystemLogService, OrderReturnRepository pOrderReturnRepository, OrderReturnItemRepository pOrderReturnItemRepository, ProductVariantService pProductVariantService, ModelMapper pModelMapper, TransactionGoodsService pTransactionGoodsService) {
        super(Order.class, OrderDTO.class, pOrderRepository);
        this.mvSendCustomerNotificationService = pSendCustomerNotificationService;
        this.mvOrderGenerateQRCodeService = pOrderGenerateQRCodeService;
        this.mvOrderReturnItemRepository = pOrderReturnItemRepository;
        this.mvTransactionGoodsService = pTransactionGoodsService;
        this.mvProductVariantService = pProductVariantService;
        this.mvLoyaltyProgramService = pLoyaltyProgramService;
        this.mvOrderReturnRepository = pOrderReturnRepository;
        this.mvVoucherTicketService = pVoucherTicketService;
        this.mvOrderHistoryService = pOrderHistoryService;
        this.mvOrderItemsService = pOrderItemsService;
        this.mvAccountRepository = pAccountRepository;
        this.mvConfigRepository = pConfigRepository;
        this.mvSystemLogService = pSystemLogService;
        this.mvCategoryService = pCategoryService;
        this.mvCustomerService = pCustomerService;
        this.mvCartService = pCartService;
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
            OrderDTO lvDto = OrderDTO.toDto(lvOrder);
            lvDto.setItems(lvDto.getListOrderDetail());
            lvDto.setTotalAmount(OrderUtils.calAmount(lvOrder));

            lvDTOs.add(lvDto);
        }

        return new PageImpl<>(lvDTOs, lvPageable, total);
    }

    @Override
    public OrderDTO findDtoById(Long pOrderId, boolean pThrowException) {
        Order lvOrderEnt = super.findEntById(pOrderId, pThrowException);
        OrderDTO lvOrderDto = super.convertDTO(lvOrderEnt);

        FileStorage imageQRCode = lvOrderEnt.getListImageQR().get(0);

        lvOrderDto.setListOrderDetail(OrderDetailDTO.fromOrderDetails(lvOrderEnt.getListOrderDetail()));
        lvOrderDto.setQrCode(FileUtils.getImageUrl(imageQRCode, false));

        return lvOrderDto;
    }

    @Override
    public Order findByCode(String pOrderCode) {
        return mvEntityRepository.findByOrderCode(pOrderCode);
    }

    @Override
    public OrderDTO findByTrackingCode(String pTrackingCode) {
        return convertDTO(mvEntityRepository.findByTrackingCode(pTrackingCode));
    }

    @Override
    public List<Order> findOrdersToday() {
        return mvEntityRepository.findByOrderTime(LocalDate.now().atStartOfDay(), LocalDate.now().atTime(LocalTime.MAX));
    }

    @Transactional
    @Override
    public OrderDTO createOrder(CreateOrderReq pRequest) {
        OrderCart lvCart = mvCartService.findEntById(pRequest.getCartId(), true);
        Map<CATEGORY, Category> lvCategoryMap = mvCategoryService.findByIdsAsMap(Set.of(pRequest.getPaymentMethodId(),
                pRequest.getSalesChannelId(), pRequest.getDeliveryMethodId()));
        
        BigDecimal lvAmountDiscount = pRequest.getAmountDiscount();
        String lvCouponCode = pRequest.getCouponCode();
        LocalDateTime lvOrderTime = getOrderTime(pRequest.getOrderTime());
        Category lvPaymentMethod = lvCategoryMap.get(CATEGORY.PAYMENT_METHOD);
        Category lvSalesChannel = lvCategoryMap.get(CATEGORY.SALES_CHANNEL);
        Category lvDeliveryMethod = lvCategoryMap.get(CATEGORY.SHIP_METHOD);
        Customer lvCustomer = mvCustomerService.findEntById(pRequest.getCustomerId(), true);
        Account lvSalesAssistant = mvAccountRepository.findById(pRequest.getSalesAssistantId())
                .orElseThrow(() -> new BadRequestException("Sales assistant invalid!"));
        
        if (ObjectUtils.isEmpty(lvCart.getListItems()))
            throw new BadRequestException("At least one product in the order!");
        if (!lvPaymentMethod.getStatus())
            throw new BadRequestException("Payment method's status invalid!");
        if (!lvSalesChannel.getStatus())
            throw new BadRequestException("Sales channel's status invalid!");
        
        if (!lvCustomer.isWalkInCustomer()) {
            if (Boolean.TRUE.equals(lvCustomer.getIsBlackList()))
                throw new BadRequestException("The customer is on the blacklist!");
            if (!CoreUtils.validateEmail(pRequest.getRecipientEmail()))
                throw new BadRequestException("Email invalid!");
            if (CoreUtils.isNullStr(pRequest.getRecipientPhone()))
                throw new BadRequestException("Phone number invalid!");
            if (CoreUtils.isNullStr(pRequest.getShippingAddress()))
                throw new BadRequestException("Address must not empty!");
        }
        
        if (lvSalesAssistant.isClosed())
            throw new BadRequestException("Sales assistant's account is closed!");

        VoucherTicket lvVoucherTicket = null;
        if (!CoreUtils.isNullStr(lvCouponCode)) {
            lvVoucherTicket = mvVoucherTicketService.findTicketByCode((lvCouponCode));
            if (lvVoucherTicket == null)
                throw new BadRequestException("Voucher invalid!");
            if (lvVoucherTicket.isUsed())
                throw new BadRequestException("Voucher code already used!");
        }

        Order order = Order.builder()
                .code(getNextOrderCode())
                .customer(lvCustomer)
                .salesChannel(lvSalesChannel)
                .nhanVienBanHang(lvSalesAssistant)
                .note(pRequest.getNote())
                .customerNote(pRequest.getCustomerNote())
                .orderTime(lvOrderTime != null ? lvOrderTime : LocalDateTime.now())
                .receiverName(pRequest.getRecipientName())
                .receiverPhone(pRequest.getRecipientPhone())
                .receiverEmail(pRequest.getRecipientEmail())
                .receiverAddress(pRequest.getShippingAddress())
                .paymentMethod(lvPaymentMethod)
                .paymentStatus(false)
                .couponCode(ObjectUtils.isNotEmpty(lvCouponCode) ? lvCouponCode : null)
                .amountDiscount(CoreUtils.coalesce(lvAmountDiscount))
                .packagingCost(CoreUtils.coalesce(pRequest.getPackagingCost(), mvDefaultPackagingCost))
                .shippingCost(CoreUtils.coalesce(pRequest.getShippingCost(), mvDefaultShippingCost))
                .deliveryMethod(lvDeliveryMethod)
                .deliveredBy("STORE_DELIVERY".equals(lvDeliveryMethod.getCode()) ? "STORE_DELIVERY" : null)
                .giftWrapCost(CoreUtils.coalesce(pRequest.getGiftWrapCost(), mvDefaultGiftWrapCost))
                .codFee(CoreUtils.coalesce(pRequest.getCodFee(), mvDefaultCodFee))
                .isGiftWrapped(false)
                .orderStatus(getDefaultOrderStatus())
                .trackingCode(generateTrackingCode())
                .build();
        order.setPriorityLevel(determinePriority(order));

        if (lvVoucherTicket != null) {
            order.setCouponCode(lvVoucherTicket.getCode());
            //Update voucher ticket's status to used
            lvVoucherTicket.setCustomer(lvCustomer);
            lvVoucherTicket.setActiveTime(new Date());
            lvVoucherTicket.setUsed(true);
            //mvVoucherTicketService.update(lvVoucherTicket, lvVoucherTicket.getId());
        }

        Order lvOrderSaved = mvEntityRepository.save(order);

        //QRCode
        try {
            mvOrderGenerateQRCodeService.generateOrderQRCode(lvOrderSaved.getId());
        } catch (IOException | WriterException e ) {
            LOG.error(String.format("Can't generate QR Code for Order %s", lvOrderSaved.getCode()), e);
        }

        //Save detail items
        List<OrderDetail> lvOrderItemsList = mvOrderItemsService.save(lvCart.getId(), lvOrderSaved.getId(), lvCart.getListItems());
        lvOrderSaved.setListOrderDetail(lvOrderItemsList);

        if (Boolean.TRUE.equals(pRequest.getAccumulateBonusPoints())) {
            LoyaltyTransaction lvLoyaltyTransaction = mvLoyaltyProgramService.accumulatePoints(lvOrderSaved, null);
            mvEntityRepository.updateLoyaltyTransaction(lvOrderSaved.getId(), lvLoyaltyTransaction);
        }

        mvCartService.markOrderFinished(lvCart.getId());

        //Log
        mvSystemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_ORD_C, MasterObject.Order, "Thêm mới đơn hàng", lvOrderSaved.getCode());
        LOG.info("Insert new order success! insertBy={}", getUserPrincipal().getUsername());

        return OrderDTO.toDto(lvOrderSaved);
    }

    private LocalDateTime getOrderTime(String pOrderTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");
        DateTimeFormatter formatterUS = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);
        LocalDateTime lvOrderTime = null;
        try {
            lvOrderTime = LocalDateTime.parse(pOrderTime, formatter);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            try {
                TemporalAccessor temporalAccessor = formatterUS.parse(pOrderTime);
                lvOrderTime = LocalDateTime.from(temporalAccessor);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return lvOrderTime;
    }

    @Transactional
    @Override
    public OrderDTO updateOrder(UpdateOrderReq request, Long pOrderId) {
        Order lvCurrentOrder = mvEntityRepository.findById(pOrderId).orElseThrow(() -> new AppException("Order not found!"));
        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(lvCurrentOrder));

        OrderStatus lvCurrentOrderStatus = lvCurrentOrder.getOrderStatus();
        OrderStatus lvRequestOrderStatus = OrderStatus.valueOf(request.getOrderStatus());
        boolean isChangeStatus = !lvCurrentOrderStatus.equals(lvRequestOrderStatus);

        /*
        * * * Validate something before update
        */
        if (isChangeStatus) {
            switch (lvRequestOrderStatus) {
                case RTND:
                    LocalDateTime lvSuccessfulDeliveryTime = request.getSuccessfulDeliveryTime();

                    SystemConfig lvReturnPeriodDaysMdl = mvConfigRepository.findByCode(ConfigCode.returnPeriodDays.name());
                    if (SysConfigUtils.isValid(lvReturnPeriodDaysMdl))
                        throw new AppException("System has not configured the time allowed to return the order!");

                    int lvReturnPeriodDays = lvReturnPeriodDaysMdl.getIntValue();
                    if (!isWithinReturnPeriod(lvSuccessfulDeliveryTime, LocalDateTime.now(), lvReturnPeriodDays))
                        throw new AppException("The return period has expired!");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + lvRequestOrderStatus);
            }
        }

        /*
         * * * Update information
         */
        lvCurrentOrder.setReceiverName(request.getRecipientName());
        lvCurrentOrder.setReceiverPhone(request.getRecipientPhone());
        lvCurrentOrder.setReceiverEmail(request.getRecipientEmail());
        lvCurrentOrder.setReceiverAddress(request.getShippingAddress());
        lvCurrentOrder.setNote(request.getNote());
        lvCurrentOrder.setOrderStatus(OrderStatus.valueOf(request.getOrderStatus()));
        Order lvUpdatedOrder = mvEntityRepository.save(lvCurrentOrder);

        /*
        * * * Do something after updated
        */
        if (isChangeStatus) {
            switch (lvUpdatedOrder.getOrderStatus()) {
                case CONF:
                    if (SysConfigUtils.isYesOption(ConfigCode.sendNotifyCustomerOnOrderConfirmation)) {
                        mvSendCustomerNotificationService.notifyOrderConfirmation(lvUpdatedOrder, lvUpdatedOrder.getReceiverEmail());
                    }
                    break;
                case RTND:
                    for (TransactionGoods lvTransactionGoods : lvUpdatedOrder.getTransactionGoodsList()) {
                        Long lvStorageId = lvTransactionGoods.getWarehouse().getId();
                        mvTransactionGoodsService.restockReturnedItems(lvStorageId, lvUpdatedOrder.getCode());
                    }
                    boolean isNeedRefund = false;
                    if (isNeedRefund) {
                        //create ledger transaction record for export
                    }
                    break;
                case DLVD:
                    mvLoyaltyProgramService.accumulatePoints(lvUpdatedOrder, null);
                    break;
            }
        }

        changeLog.setNewObject(lvUpdatedOrder);
        changeLog.doAudit();

        /*
        * * * Log
        */
        mvOrderHistoryService.save(changeLog.getLogChanges(), "Cập nhật đơn hàng", pOrderId, null);
        mvSystemLogService.writeLogUpdate(MODULE.SALES, ACTION.PRO_ORD_U, MasterObject.Order, "Cập nhật đơn hàng", changeLog);
        LOG.info("Cập nhật đơn hàng {}", lvUpdatedOrder.toString());

        return OrderDTO.toDto(lvUpdatedOrder);
    }

    @Override
    public String deleteOrder(Long id) {
        Order lvOrder = super.findEntById(id, true);
        if (Boolean.TRUE.equals(lvOrder.getPaymentStatus())) {
            throw new DataInUseException(ErrorCode.ERROR_DATA_LOCKED.getDescription());
        }

        mvEntityRepository.deleteById(id);

        mvSystemLogService.writeLogDelete(
                MODULE.PRODUCT,
                ACTION.PRO_ORD_D,
                MasterObject.Order,
                "Xóa đơn hàng",
                lvOrder.toString()
        );
        LOG.info("Đã xóa đơn hàng với ID={}", id);

        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public String updateOrderStatus(Long pOrderId, OrderStatus pOrderStatus, LocalDateTime pSuccessfulDeliveryTime, Long cancellationReasonId) {
        Order lvOrder = super.findById(pOrderId).orElseThrow(() -> new BadRequestException("Order not found!"));

        if (lvOrder.getOrderStatus().equals(pOrderStatus)) {
            throw new BadRequestException(String.format("Order status is %s now!", pOrderStatus.getName()));
        }

        lvOrder.setOrderStatus(pOrderStatus);

        switch (pOrderStatus) {
            case DLVD:
                lvOrder.setDeliverySuccessTime(pSuccessfulDeliveryTime != null ? pSuccessfulDeliveryTime : LocalDateTime.now());
                break;
            case CNCL:
                lvOrder.setCancellationReason(cancellationReasonId);
                lvOrder.setCancellationDate(LocalDateTime.now());
                break;
            default:
                // Do nothing or log if needed
                break;
        }

        mvEntityRepository.save(lvOrder);
        return "Updated successfully!";
    }

    @Override
    public void doCancel(OrderDTO pOrder, String pReason) {
        Order lvOrder = super.findEntById(pOrder.getId(), true);
        lvOrder.setCancellationReason(null);
        lvOrder.setCancellationDate(LocalDateTime.now());
        Order lvOrderUpdated = mvEntityRepository.save(lvOrder);

        if (SysConfigUtils.isYesOption(ConfigCode.sendNotifyCustomerOnOrderConfirmation)) {
            mvSendCustomerNotificationService.notifyOrderConfirmation(lvOrderUpdated, lvOrderUpdated.getReceiverEmail());
        }

        //...
    }

    @Override
    public void doComplete(OrderDTO pOrder) {
        Order lvOrder = super.findEntById(pOrder.getId(), true);
        lvOrder.setOrderStatus(OrderStatus.DLVD);
        lvOrder.setDeliverySuccessTime(LocalDateTime.now());
        Order lvOrderUpdated = mvEntityRepository.save(lvOrder);

        Long lvProgramId = null;
        mvLoyaltyProgramService.accumulatePoints(lvOrderUpdated, lvProgramId);

        //...
    }

    @Transactional
    @Override
    public void doReturn(OrderReturnReq pRequest) throws Exception {
        Order lvOrder = super.findEntById(pRequest.getOrderId(), true);
        List<OrderDetail> lvOriginalItems = lvOrder.getListOrderDetail();
        boolean isReturnAllItems = false;

        OrderReturn lvReturnsRecord = mvOrderReturnRepository.save(OrderReturn.builder()
                .order(lvOrder)
                .returnsCode(getReturnsCode())
                .reason(pRequest.getReason())
                .returnDate(LocalDateTime.now())
                .refundMethod(pRequest.getRefundMethod() != null ? pRequest.getRefundMethod() : OrderRefundMethod.BANKING)
                .refundAmount(pRequest.getRefundAmount() != null ? pRequest.getRefundAmount() : new BigDecimal(-1))
                .isRefunded(pRequest.getIsRefunded() != null ? pRequest.getIsRefunded() : false)
                .status(pRequest.getStatus() != null ? pRequest.getStatus() : OrderReturnStatus.PENDING)
                .build());

        List<OrderReturnItem> lvRecordedItems = new ArrayList<>();
        if (!CollectionUtils.isEmpty(pRequest.getItems())) {
            lvRecordedItems.addAll(mvOrderReturnItemRepository.saveAll(pRequest.getItems().stream()
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
                        mvOrderItemsService.updateReturnsStatus(lvItem.getItemId(), true);
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

        //Put items back into storage/inventory
        {
//            Long lvStorageId = -1L;
//            String lvOrderCode = lvOrder.getCode();
//            mvTicketImportService.restockReturnedItems(lvStorageId, lvOrderCode);
//
//            TransactionGoodsDTO dto = new TransactionGoodsDTO();
//            dto.setId(null);
//            dto.setType(TransactionGoodsType.RECEIPT.getValue());
//            dto.setDescription(null);
//
//            mvTransactionGoodsService.createTransactionGoods(dto);
        }

        //Save attached images
        {

        }

        lvOrder.setOrderStatus(isReturnAllItems ? OrderStatus.FRTND : OrderStatus.RTND);
        mvEntityRepository.save(lvOrder);

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
                    lvDto.setSeller(lvOrderOriginal.getNhanVienBanHang().getFullName());
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

    @Override
    public void doRefund(Long pOrderId) {
        //...
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

    private OrderStatus getDefaultOrderStatus() {
        return OrderStatus.PEND;
    }

    public boolean isWithinReturnPeriod(LocalDateTime successfulDeliveryTime, LocalDateTime currentDay, int periodDays) {
        long daysBetween = Duration.between(successfulDeliveryTime, currentDay).toDays();
        return daysBetween < periodDays;
    }

    public PriorityLevel determinePriority(Order order) {
        if (Boolean.TRUE.equals(order.getCustomer().getIsVIP())) {
            return PriorityLevel.H;
        }
        //Do more case here...
        return PriorityLevel.M;
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

    private String getReturnsCode() {
        OrderReturn lvLatestOrderReturn = mvOrderReturnRepository.findTopByOrderByIdDesc();
        String lvLatestCode = lvLatestOrderReturn == null ? "" : CoreUtils.trim(lvLatestOrderReturn.getReturnsCode());
        if (CoreUtils.isNullStr(lvLatestCode)) {
            return PREFIX_RETURNS_ORDER_CODE + "00001";
        }
        int lvCurrentIndex = Integer.parseInt(lvLatestCode.substring(PREFIX_RETURNS_ORDER_CODE.length()));
        return PREFIX_RETURNS_ORDER_CODE + (lvCurrentIndex + 1);
    }
}