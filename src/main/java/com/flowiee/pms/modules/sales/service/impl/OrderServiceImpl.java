package com.flowiee.pms.modules.sales.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.common.utils.*;
import com.flowiee.pms.modules.inventory.service.TicketImportService;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.modules.sales.entity.VoucherTicket;
import com.flowiee.pms.modules.sales.entity.Customer;
import com.flowiee.pms.modules.sales.entity.OrderCart;
import com.flowiee.pms.modules.sales.entity.OrderDetail;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.sales.service.*;
import com.flowiee.pms.modules.staff.entity.Account;
import com.flowiee.pms.modules.staff.repository.AccountRepository;
import com.flowiee.pms.modules.system.entity.SystemConfig;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.sales.dto.OrderDetailDTO;
import com.flowiee.pms.modules.sales.model.CreateOrderReq;
import com.flowiee.pms.modules.sales.model.UpdateOrderReq;
import com.flowiee.pms.modules.sales.repository.CustomerRepository;
import com.flowiee.pms.modules.system.repository.ConfigRepository;
import com.flowiee.pms.common.security.UserSession;
import com.flowiee.pms.modules.system.service.CategoryService;
import com.flowiee.pms.modules.system.service.SendCustomerNotificationService;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.modules.sales.dto.OrderDTO;
import com.flowiee.pms.common.exception.DataInUseException;
import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.modules.sales.repository.OrderRepository;

import com.flowiee.pms.modules.system.service.SystemLogService;
import com.google.zxing.WriterException;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

@Service
public class OrderServiceImpl extends BaseService<Order, OrderDTO, OrderRepository> implements OrderReadService, OrderWriteService {
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private final SendCustomerNotificationService mvSendCustomerNotificationService;
    private final CartService mvCartService;
    private final ConfigRepository mvConfigRepository;
    private final CartItemsService mvCartItemsService;
    private final OrderItemsService mvOrderItemsService;
    private final OrderGenerateQRCodeService mvOrderGenerateQRCodeService;
    private final CustomerRepository  mvCustomerRepository;
    private final OrderHistoryService mvOrderHistoryService;
    private final TicketImportService mvTicketImportService;
    private final VoucherTicketService mvVoucherTicketService;
    private final LoyaltyProgramService mvLoyaltyProgramService;
    private final CategoryService mvCategoryService;
    private final CustomerService mvCustomerService;
    private final AccountRepository mvAccountRepository;
    private final UserSession     mvUserSession;
    private final SystemLogService mvSystemLogService;

    public OrderServiceImpl(OrderRepository pOrderRepository, SendCustomerNotificationService mvSendCustomerNotificationService, CartService mvCartService, ConfigRepository mvConfigRepository, CartItemsService mvCartItemsService, OrderItemsService mvOrderItemsService, OrderGenerateQRCodeService mvOrderGenerateQRCodeService, CustomerRepository mvCustomerRepository, OrderHistoryService mvOrderHistoryService, TicketImportService mvTicketImportService, VoucherTicketService mvVoucherTicketService, LoyaltyProgramService mvLoyaltyProgramService, CategoryService mvCategoryService, CustomerService mvCustomerService, AccountRepository pAccountRepository, UserSession mvUserSession, SystemLogService pSystemLogService) {
        super(Order.class, OrderDTO.class, pOrderRepository);
        this.mvSendCustomerNotificationService = mvSendCustomerNotificationService;
        this.mvCartService = mvCartService;
        this.mvConfigRepository = mvConfigRepository;
        this.mvCartItemsService = mvCartItemsService;
        this.mvOrderItemsService = mvOrderItemsService;
        this.mvOrderGenerateQRCodeService = mvOrderGenerateQRCodeService;
        this.mvCustomerRepository = mvCustomerRepository;
        this.mvOrderHistoryService = mvOrderHistoryService;
        this.mvTicketImportService = mvTicketImportService;
        this.mvVoucherTicketService = mvVoucherTicketService;
        this.mvLoyaltyProgramService = mvLoyaltyProgramService;
        this.mvCategoryService = mvCategoryService;
        this.mvCustomerService = mvCustomerService;
        this.mvAccountRepository = pAccountRepository;
        this.mvUserSession = mvUserSession;
        this.mvSystemLogService = pSystemLogService;
    }

    private BigDecimal mvDefaultShippingCost = BigDecimal.ZERO;
    private BigDecimal mvDefaultPackagingCost = BigDecimal.ZERO;
    private BigDecimal mvDefaultGiftWrapCost = BigDecimal.ZERO;
    private BigDecimal mvDefaultCodFee = BigDecimal.ZERO;

    private static final int GENERATE_TRACKING_CODE_MAX_RETRIES = 30;

    @Override
    public List<OrderDTO>find() {
        return findAll(-1, -1, null, null, null, null, null, null, null, null, null, null, null, null, null).getContent();
    }

    @Override
    public Page<OrderDTO> findAll(int pPageSize, int pPageNum, String pTxtSearch, Long pOrderId, Long pPaymentMethodId,
                                  OrderStatus pOrderStatus, Long pSalesChannelId, Long pSellerId, Long pCustomerId,
                                  Long pBranchId, Long pGroupCustomerId, String pDateFilter, LocalDateTime pOrderTimeFrom, LocalDateTime pOrderTimeTo, String pSortBy) {
        Pageable lvPageable = getPageable(pPageNum, pPageSize, Sort.by(pSortBy != null ? pSortBy : "orderTime").descending());
        LocalDateTime lvOrderTimeFrom = DateTimeUtil.getFilterStartTime(pOrderTimeFrom);
        LocalDateTime lvOrderTimeTo = DateTimeUtil.getFilterEndTime(pOrderTimeTo);
        if (!CoreUtils.isNullStr(pDateFilter)) {
            LocalDateTime[] lvFromDateToDate = DateTimeUtil.getFromDateToDate(lvOrderTimeFrom, lvOrderTimeTo, pDateFilter);
            lvOrderTimeFrom = lvFromDateToDate[0];
            lvOrderTimeTo = lvFromDateToDate[1];
        }

        QueryBuilder<Order> lvQueryBuilder = createQueryBuilder(Order.class)
                .addEqual("id", pOrderId)
                .addEqual("paymentMethod.id", pPaymentMethodId)
                .addEqual("orderStatus", pOrderStatus)
                .addEqual("salesChannel.id", pSalesChannelId)
                .addEqual("customer.id", pCustomerId)
                .addEqual("nhanVienBanHang.id", pSellerId)
                .addEqual("nhanVienBanHang.branch.id", pBranchId)
                .addEqual("customer.groupCustomer.id", pGroupCustomerId)
                .addLike(pTxtSearch, "receiverName", "receiverPhone", "code")
                .addBetween("orderTime", lvOrderTimeFrom, lvOrderTimeTo);
        List<Order> lvResultList = lvQueryBuilder.build(lvPageable).getResultList();
        long total = lvQueryBuilder.buildCount();

        List<OrderDTO> lvResultListDto = OrderDTO.fromOrders(lvResultList);
        for (OrderDTO lvOrder : lvResultListDto) {
            lvOrder.setItems(lvOrder.getListOrderDetail());
        }

        return new PageImpl<>(lvResultListDto, lvPageable, total);
    }

    @Override
    public OrderDTO findById(Long pOrderId, boolean pThrowException) {
        Order lvOrderEnt = super.findEntById(pOrderId, pThrowException);
        OrderDTO lvOrderDto = super.convertDTO(lvOrderEnt);

        FileStorage imageQRCode = lvOrderEnt.getListImageQR().get(0);

        lvOrderDto.setListOrderDetail(OrderDetailDTO.fromOrderDetails(lvOrderEnt.getListOrderDetail()));
        lvOrderDto.setQrCode(FileUtils.getImageUrl(imageQRCode, false));

        return lvOrderDto;
    }

    @Override
    public OrderDTO findByTrackingCode(String pTrackingCode) {
        return convertDTO(mvEntityRepository.findByTrackingCode(pTrackingCode));
    }

    @Transactional
    @Override
    public OrderDTO createOrder(CreateOrderReq pRequest) {
        BigDecimal lvAmountDiscount = pRequest.getAmountDiscount();
        String lvCouponCode = pRequest.getCouponCode();
        LocalDateTime lvOrderTime = getOrderTime(pRequest.getOrderTime());

        OrderCart lvCart = mvCartService.findEntById(pRequest.getCartId(), true);
        if (ObjectUtils.isEmpty(lvCart.getListItems())) throw new BadRequestException("At least one product in the order!");

        Category lvPaymentMethod = mvCategoryService.findEntById(pRequest.getPaymentMethodId(), true);
        if (!lvPaymentMethod.getStatus()) throw new BadRequestException("Payment method's status invalid!");

        Category lvSalesChannel = mvCategoryService.findEntById(pRequest.getSalesChannelId(), true);
        if (!lvSalesChannel.getStatus()) throw new BadRequestException("Sales channel's status invalid!");

        Customer lvCustomer = mvCustomerService.findEntById(pRequest.getCustomerId(), true);
        if (!lvCustomer.isWalkInCustomer()) {
            if (Boolean.TRUE.equals(lvCustomer.getIsBlackList())) throw new BadRequestException("The customer is on the blacklist!");
            if (!CoreUtils.validateEmail(pRequest.getRecipientEmail())) throw new BadRequestException("Email invalid!");
            if (!CoreUtils.validatePhoneNumber(pRequest.getRecipientPhone(), CommonUtils.defaultCountryCode)) throw new BadRequestException("Phone number invalid!");
            if (CoreUtils.isNullStr(pRequest.getShippingAddress())) throw new BadRequestException("Address must not empty!");
        }

        Account lvSalesAssistant = mvAccountRepository.findById(pRequest.getSalesAssistantId())
                .orElseThrow(() -> new BadRequestException("Sales assistant invalid!"));
        if (lvSalesAssistant.isClosed()) {
            throw new BadRequestException("Sales assistant's account is closed!");
        }

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

        //Create QRCode
        try {
            mvOrderGenerateQRCodeService.generateOrderQRCode(lvOrderSaved.getId());
        } catch (IOException | WriterException e ) {
            e.printStackTrace();
            LOG.error(String.format("Can't generate QR Code for Order %s", lvOrderSaved.getCode()), e);
        }

        //Save detail items
        List<OrderDetail> lvOrderItemsList = mvOrderItemsService.save(lvCart.getId(), lvOrderSaved.getId(), lvCart.getListItems());
        BigDecimal totalAmountDiscount = OrderUtils.calAmount(lvOrderItemsList, lvOrderSaved.getAmountDiscount());
        if (totalAmountDiscount.doubleValue() <= 0) {
            throw new BadRequestException("The value of order must greater than zero!");
        }

        //Accumulate bonus points for customer
        if (pRequest.getAccumulateBonusPoints() != null && pRequest.getAccumulateBonusPoints()) {
            int bonusPoints = OrderUtils.calBonusPoints(totalAmountDiscount.subtract(lvOrderSaved.getAmountDiscount()));
            mvCustomerRepository.updateBonusPoint(lvCustomer.getId(), bonusPoints);
        }

        mvCartService.markOrderFinished(lvCart.getId());

        //Log
        mvSystemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_ORD_C, MasterObject.Order, "Thêm mới đơn hàng", lvOrderSaved.getCode());
        LOG.info("Insert new order success! insertBy={}", mvUserSession.getUserPrincipal().getUsername());

        return OrderDTO.fromOrder(lvOrderSaved);
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
                    Long lvStorageId = lvUpdatedOrder.getTicketExport().getStorage().getId();
                    mvTicketImportService.restockReturnedItems(lvStorageId, lvUpdatedOrder.getCode());
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

        return OrderDTO.fromOrder(lvUpdatedOrder);
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
    public List<Order> findOrdersToday() {
        return mvEntityRepository.findOrdersToday();
    }

    @Override
    public Page<OrderDTO> getOrdersByCustomer(int pageSize, int pageNum, Long pCustomerId) {
        return this.findAll(pageSize, pageNum, null, null, null, null, null, null, pCustomerId, null, null, null, null, null, null);
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
    public Order getOrderByCode(String pOrderCode) {
        return mvEntityRepository.findByOrderCode(pOrderCode);
    }

    private String getNextOrderCode() {
        int orderTodayQty = 0;
        List<Order> ordersToday = mvEntityRepository.findOrdersToday();
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
}