package com.flowiee.pms.modules.sales.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.utils.*;
import com.flowiee.pms.modules.inventory.dto.TransactionGoodsDTO;
import com.flowiee.pms.modules.inventory.service.TicketImportService;
import com.flowiee.pms.modules.inventory.service.TransactionGoodsService;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.modules.sales.entity.VoucherTicket;
import com.flowiee.pms.modules.sales.entity.Customer;
import com.flowiee.pms.modules.sales.entity.OrderCart;
import com.flowiee.pms.modules.sales.entity.OrderDetail;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.sales.model.OrderReq;
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

@Service
public class OrderServiceImpl extends BaseService<Order, OrderDTO, OrderRepository> implements OrderService {
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private final SendCustomerNotificationService mvSendCustomerNotificationService;
    private final OrderGenerateQRCodeService mvOrderGenerateQRCodeService;
    private final TransactionGoodsService mvTransactionGoodsService;
    private final CartService mvCartService;
    private final ConfigRepository mvConfigRepository;
    private final OrderItemsService mvOrderItemsService;
    private final CustomerRepository  mvCustomerRepository;
    private final OrderHistoryService mvOrderHistoryService;
    private final TicketImportService mvTicketImportService;
    private final VoucherTicketService mvVoucherTicketService;
    private final LoyaltyProgramService mvLoyaltyProgramService;
    private final CategoryService mvCategoryService;
    private final CustomerService mvCustomerService;
    private final AccountRepository mvAccountRepository;
    private final SystemLogService mvSystemLogService;

    public OrderServiceImpl(OrderRepository pOrderRepository, SendCustomerNotificationService mvSendCustomerNotificationService, CartService mvCartService, ConfigRepository mvConfigRepository, OrderItemsService mvOrderItemsService, OrderGenerateQRCodeService mvOrderGenerateQRCodeService, CustomerRepository mvCustomerRepository, OrderHistoryService mvOrderHistoryService, TicketImportService mvTicketImportService, VoucherTicketService mvVoucherTicketService, LoyaltyProgramService mvLoyaltyProgramService, CategoryService mvCategoryService, CustomerService mvCustomerService, AccountRepository pAccountRepository, SystemLogService pSystemLogService, TransactionGoodsService pTransactionGoodsService) {
        super(Order.class, OrderDTO.class, pOrderRepository);
        this.mvSendCustomerNotificationService = mvSendCustomerNotificationService;
        this.mvOrderGenerateQRCodeService = mvOrderGenerateQRCodeService;
        this.mvTransactionGoodsService = pTransactionGoodsService;
        this.mvCartService = mvCartService;
        this.mvConfigRepository = mvConfigRepository;
        this.mvOrderItemsService = mvOrderItemsService;
        this.mvCustomerRepository = mvCustomerRepository;
        this.mvOrderHistoryService = mvOrderHistoryService;
        this.mvTicketImportService = mvTicketImportService;
        this.mvVoucherTicketService = mvVoucherTicketService;
        this.mvLoyaltyProgramService = mvLoyaltyProgramService;
        this.mvCategoryService = mvCategoryService;
        this.mvCustomerService = mvCustomerService;
        this.mvAccountRepository = pAccountRepository;
        this.mvSystemLogService = pSystemLogService;
    }

    private final BigDecimal mvDefaultShippingCost = BigDecimal.ZERO;
    private final BigDecimal mvDefaultPackagingCost = BigDecimal.ZERO;
    private final BigDecimal mvDefaultGiftWrapCost = BigDecimal.ZERO;
    private final BigDecimal mvDefaultCodFee = BigDecimal.ZERO;

    private static final int GENERATE_TRACKING_CODE_MAX_RETRIES = 30;

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
            OrderDTO lvDto = OrderDTO.fromOrder(lvOrder);
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
                pRequest.getSalesChannelId()));
        
        BigDecimal lvAmountDiscount = pRequest.getAmountDiscount();
        String lvCouponCode = pRequest.getCouponCode();
        LocalDateTime lvOrderTime = getOrderTime(pRequest.getOrderTime());
        Category lvPaymentMethod = lvCategoryMap.get(CATEGORY.PAYMENT_METHOD);
        Category lvSalesChannel = lvCategoryMap.get(CATEGORY.SALES_CHANNEL);
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
            if (!CoreUtils.validatePhoneNumber(pRequest.getRecipientPhone(), CommonUtils.defaultCountryCode))
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
        LOG.info("Insert new order success! insertBy={}", getUserPrincipal().getUsername());

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

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void doReturn(OrderDTO pOrder) {
        try {
            Order lvOrder = super.findEntById(pOrder.getId(), true);
            Long lvStorageId = null;
            String lvOrderCode = lvOrder.getCode();
            mvTicketImportService.restockReturnedItems(lvStorageId, lvOrderCode);

            TransactionGoodsDTO dto = new TransactionGoodsDTO();
            dto.setId(null);
            dto.setType(TransactionGoodsType.RECEIPT.getValue());
            dto.setDescription(null);

            mvTransactionGoodsService.createTransactionGoods(dto);
        } catch (Exception e) {
            LOG.error("Cancel order got [{}]", e.getMessage(), e);
        }
    }

    @Override
    public void doRefund(Long pOrderId) {
        Order lvOrder = super.findEntById(pOrderId, true);
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
}