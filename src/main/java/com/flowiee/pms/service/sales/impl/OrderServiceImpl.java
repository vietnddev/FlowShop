package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.base.service.BaseGService;
import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.entity.sales.*;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.entity.system.Account;
import com.flowiee.pms.entity.system.SystemConfig;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.model.dto.OrderDetailDTO;
import com.flowiee.pms.model.payload.CreateOrderReq;
import com.flowiee.pms.model.payload.UpdateOrderReq;
import com.flowiee.pms.repository.category.CategoryRepository;
import com.flowiee.pms.repository.sales.CustomerRepository;
import com.flowiee.pms.repository.system.ConfigRepository;
import com.flowiee.pms.security.UserSession;
import com.flowiee.pms.service.category.CategoryService;
import com.flowiee.pms.service.system.AccountService;
import com.flowiee.pms.service.system.SendCustomerNotificationService;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.model.dto.OrderDTO;
import com.flowiee.pms.exception.DataInUseException;
import com.flowiee.pms.service.sales.*;
import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.repository.sales.OrderRepository;

import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.OrderUtils;
import com.flowiee.pms.service.system.SystemLogService;
import com.google.zxing.WriterException;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.*;

@Service
public class OrderServiceImpl extends BaseGService<Order, OrderDTO, OrderRepository> implements OrderReadService, OrderWriteService {
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private final SendCustomerNotificationService mvSendCustomerNotificationService;
    private final CartService           mvCartService;
    //private final OrderRepository       mvOrderRepository;
    private final ConfigRepository      mvConfigRepository;
    private final CartItemsService      mvCartItemsService;
    private final OrderItemsService          mvOrderItemsService;
    private final OrderGenerateQRCodeService mvOrderGenerateQRCodeService;
    private final CustomerRepository         mvCustomerRepository;
    private final OrderHistoryService   mvOrderHistoryService;
    private final TicketImportService   mvTicketImportService;
    private final VoucherTicketService  mvVoucherTicketService;
    private final LoyaltyProgramService mvLoyaltyProgramService;
    private final CategoryService       mvCategoryService;
    private final CategoryRepository    mvCategoryRepository;
    private final CustomerService       mvCustomerService;
    private final AccountService        mvAccountService;
    private final UserSession           mvUserSession;
    private final ModelMapper           mvModelMapper;
    private final SystemLogService      mvSystemLogService;

    public OrderServiceImpl(OrderRepository pOrderRepository, SendCustomerNotificationService mvSendCustomerNotificationService, CartService mvCartService, ConfigRepository mvConfigRepository, CartItemsService mvCartItemsService, OrderItemsService mvOrderItemsService, OrderGenerateQRCodeService mvOrderGenerateQRCodeService, CustomerRepository mvCustomerRepository, OrderHistoryService mvOrderHistoryService, TicketImportService mvTicketImportService, VoucherTicketService mvVoucherTicketService, LoyaltyProgramService mvLoyaltyProgramService, CategoryService mvCategoryService, CategoryRepository mvCategoryRepository, CustomerService mvCustomerService, AccountService mvAccountService, UserSession mvUserSession, ModelMapper mvModelMapper, SystemLogService pSystemLogService) {
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
        this.mvCategoryRepository = mvCategoryRepository;
        this.mvCustomerService = mvCustomerService;
        this.mvAccountService = mvAccountService;
        this.mvUserSession = mvUserSession;
        this.mvModelMapper = mvModelMapper;
        this.mvSystemLogService = pSystemLogService;
    }

    private BigDecimal mvDefaultShippingCost = BigDecimal.ZERO;
    private BigDecimal mvDefaultPackagingCost = BigDecimal.ZERO;
    private BigDecimal mvDefaultGiftWrapCost = BigDecimal.ZERO;
    private BigDecimal mvDefaultCodFee = BigDecimal.ZERO;

    private static final int GENERATE_TRACKING_CODE_MAX_RETRIES = 30;

    @Override
    public List<OrderDTO> findAll() {
        return findAll(-1, -1, null, null, null, null, null, null, null, null, null, null, null, null, null).getContent();
    }

    @Override
    public Page<OrderDTO> findAll(int pPageSize, int pPageNum, String pTxtSearch, Long pOrderId, Long pPaymentMethodId,
                                  OrderStatus pOrderStatus, Long pSalesChannelId, Long pSellerId, Long pCustomerId,
                                  Long pBranchId, Long pGroupCustomerId, String pDateFilter, LocalDateTime pOrderTimeFrom, LocalDateTime pOrderTimeTo, String pSortBy) {
        Pageable lvPageable = getPageable(pPageNum, pPageSize, Sort.by(pSortBy != null ? pSortBy : "orderTime").descending());
        LocalDateTime lvOrderTimeFrom = getFilterStartTime(pOrderTimeFrom);
        LocalDateTime lvOrderTimeTo = getFilterEndTime(pOrderTimeTo);
        if (!CoreUtils.isNullStr(pDateFilter)) {
            LocalDateTime[] lvFromDateToDate = getFromDateToDate(lvOrderTimeFrom, lvOrderTimeTo, pDateFilter);
            lvOrderTimeFrom = lvFromDateToDate[0];
            lvOrderTimeTo = lvFromDateToDate[1];
        }

        CriteriaBuilder lvCriteriaBuilder = mvEntityManager.getCriteriaBuilder();
        CriteriaQuery<Order> lvCriteriaQuery = lvCriteriaBuilder.createQuery(Order.class);
        Root<Order> lvRoot = lvCriteriaQuery.from(Order.class);

        List<Predicate> lvPredicates = new ArrayList<>();
        addEqualCondition(lvCriteriaBuilder, lvPredicates, lvRoot.get("id"), pOrderId);
        addEqualCondition(lvCriteriaBuilder, lvPredicates, lvRoot.get("paymentMethod").get("id"), pPaymentMethodId);
        addEqualCondition(lvCriteriaBuilder, lvPredicates, lvRoot.get("orderStatus"), pOrderStatus);
        addEqualCondition(lvCriteriaBuilder, lvPredicates, lvRoot.get("salesChannel").get("id"), pSalesChannelId);
        addEqualCondition(lvCriteriaBuilder, lvPredicates, lvRoot.get("customer").get("id"), pCustomerId);
        addEqualCondition(lvCriteriaBuilder, lvPredicates, lvRoot.get("nhanVienBanHang").get("id"), pSellerId);
        addEqualCondition(lvCriteriaBuilder, lvPredicates, lvRoot.get("nhanVienBanHang").get("branch").get("id"), pBranchId);
        addEqualCondition(lvCriteriaBuilder, lvPredicates, lvRoot.get("customer").get("groupCustomer").get("id"), pGroupCustomerId);
        addLikeCondition(lvCriteriaBuilder, lvPredicates, pTxtSearch,
                lvRoot.get("receiverName"), lvRoot.get("receiverPhone"), lvRoot.get("code"));
        addBetweenCondition(lvCriteriaBuilder, lvPredicates, lvRoot.get("orderTime"), "trunc", LocalDateTime.class,
                lvOrderTimeFrom, lvOrderTimeTo);

        TypedQuery<Order> lvTypedQuery = initCriteriaQuery(lvCriteriaBuilder, lvCriteriaQuery, lvRoot, lvPredicates, lvPageable);
        TypedQuery<Long> lvCountQuery = initCriteriaCountQuery(lvCriteriaBuilder, lvPredicates, Order.class);
        long total = lvCountQuery.getSingleResult();

        List<Order> lvResultList = lvTypedQuery.getResultList();
        List<OrderDTO> lvResultListDto = OrderDTO.fromOrders(lvResultList);

        return new PageImpl<>(lvResultListDto, lvPageable, total);
    }

    @Override
    public OrderDTO findById(Long pOrderId, boolean pThrowException) {
        OrderDTO lvOrderDto = super.findById(pOrderId, pThrowException);

        //Set ListOrderDetail -> Have to enhance in the next version
        Order lvOrder = mvModelMapper.map(lvOrderDto, Order.class);
        lvOrderDto.setListOrderDetail(OrderDetailDTO.fromOrderDetails(lvOrder.getListOrderDetail()));

        return lvOrderDto;
    }

    @Override
    public OrderDTO findByTrackingCode(String pTrackingCode) {
        return convertDTO(mvEntityRepository.findByTrackingCode(pTrackingCode));
    }

    private VldModel vldBeforeCreateOrder(CreateOrderReq pOrderRequest, Long pCartId, String pVoucherCode, Long pPaymentMethodId, Long pSaleChannelId, Long pCustomerId, Long pSalesAssistantId) {
        OrderCart lvCart = mvCartService.findById(pCartId, true);
        if (ObjectUtils.isEmpty(lvCart.getListItems()))
            throw new BadRequestException("At least one product in the order!");

        Category lvPaymentMethod = mvCategoryRepository.findById(pPaymentMethodId).get();
        if (lvPaymentMethod == null || !lvPaymentMethod.getStatus())
            throw new BadRequestException("Payment method invalid!");
        Category lvSalesChannel = mvCategoryRepository.findById(pSaleChannelId).get();
        if (lvSalesChannel == null || !lvSalesChannel.getStatus())
            throw new BadRequestException("Sales channel invalid!");

        Customer lvCustomer = mvCustomerService.findById(pCustomerId, true);
        if (!lvCustomer.isWalkInCustomer()) {
            if (lvCustomer.getIsBlackList())
                throw new BadRequestException("The customer is on the blacklist!");
            if (!CoreUtils.validateEmail(pOrderRequest.getRecipientEmail()))
                throw new BadRequestException("Email invalid!");
            if (!CoreUtils.validatePhoneNumber(pOrderRequest.getRecipientPhone(), CommonUtils.defaultCountryCode))
                throw new BadRequestException("Phone number invalid!");
            if (CoreUtils.isNullStr(pOrderRequest.getShippingAddress()))
                throw new BadRequestException("Address must not empty!");
        }

        Account lvSalesAssistant = mvAccountService.findById(pSalesAssistantId, false);
        if (lvSalesAssistant == null || lvSalesAssistant.isClosed())
            throw new BadRequestException("Sales assistant invalid!");

        VoucherTicket lvVoucherTicket = null;
        if (!CoreUtils.isNullStr(pVoucherCode)) {
            lvVoucherTicket = mvVoucherTicketService.findTicketByCode((pVoucherCode));
            if (lvVoucherTicket == null)
                throw new BadRequestException("Voucher invalid!");
            if (lvVoucherTicket.isUsed())
                throw new BadRequestException("Voucher code already used!");
        }

        VldModel vldModel = new VldModel();
        vldModel.setSalesAssistant(lvSalesAssistant);
        vldModel.setPaymentMethod(lvPaymentMethod);
        vldModel.setSalesChannel(lvSalesChannel);
        vldModel.setCustomer(lvCustomer);
        vldModel.setOrderCart(lvCart);
        vldModel.setVoucherTicket(lvVoucherTicket);

        return vldModel;
    }

    @Transactional
    @Override
    public OrderDTO createOrder(CreateOrderReq request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");
        DateTimeFormatter formatterUS = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

        BigDecimal lvAmountDiscount = request.getAmountDiscount();
        String lvCouponCode = request.getCouponCode();
        LocalDateTime lvOrderTime = null;
        try {
            lvOrderTime = LocalDateTime.parse(request.getOrderTime(), formatter);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            try {
                TemporalAccessor temporalAccessor = formatterUS.parse(request.getOrderTime());
                lvOrderTime = LocalDateTime.from(temporalAccessor);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        Long lvCartId = request.getCartId();

        VldModel vldModel = vldBeforeCreateOrder(request, lvCartId, lvCouponCode, request.getPaymentMethodId(),
                request.getSalesChannelId(), request.getCustomerId(), request.getSalesAssistantId());
        OrderCart lvCart = vldModel.getOrderCart();
        VoucherTicket lvVoucherTicket = vldModel.getVoucherTicket();
        Customer lvCustomer = vldModel.getCustomer();

        Order order = Order.builder()
                .code(getNextOrderCode())
                .customer(lvCustomer)
                .salesChannel(vldModel.getSalesChannel())
                .nhanVienBanHang(vldModel.getSalesAssistant())
                .note(request.getNote())
                .orderTime(lvOrderTime != null ? lvOrderTime : LocalDateTime.now())
                .receiverName(request.getRecipientName())
                .receiverPhone(request.getRecipientPhone())
                .receiverEmail(request.getRecipientEmail())
                .receiverAddress(request.getShippingAddress())
                .paymentMethod(vldModel.getPaymentMethod())
                .paymentStatus(false)
                .couponCode(ObjectUtils.isNotEmpty(lvCouponCode) ? lvCouponCode : null)
                .amountDiscount(CoreUtils.coalesce(lvAmountDiscount))
                .packagingCost(CoreUtils.coalesce(request.getPackagingCost(), mvDefaultPackagingCost))
                .shippingCost(CoreUtils.coalesce(request.getShippingCost(), mvDefaultShippingCost))
                .giftWrapCost(CoreUtils.coalesce(request.getGiftWrapCost(), mvDefaultGiftWrapCost))
                .codFee(CoreUtils.coalesce(request.getCodFee(), mvDefaultCodFee))
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
            mvVoucherTicketService.update(lvVoucherTicket, lvVoucherTicket.getId());
        }

        //Create order
        Order lvOrderSaved = mvEntityRepository.save(order);
        //Create QRCode
        try {
            mvOrderGenerateQRCodeService.generateOrderQRCode(lvOrderSaved.getId());
        } catch (IOException | WriterException e ) {
            e.printStackTrace();
            LOG.error(String.format("Can't generate QR Code for Order %s", lvOrderSaved.getCode()), e);
        }
        //Create items detail
        List<OrderDetail> lvOrderItemsList = mvOrderItemsService.save(lvCart.getId(), lvOrderSaved.getId(), lvCart.getListItems());
        BigDecimal totalAmountDiscount = OrderUtils.calTotalAmount_(lvOrderItemsList, lvOrderSaved.getAmountDiscount());
        if (totalAmountDiscount.doubleValue() <= 0) {
            throw new BadRequestException("The value of order must greater than zero!");
        }

        //Accumulate bonus points for customer
        if (request.getAccumulateBonusPoints() != null && request.getAccumulateBonusPoints()) {
            int bonusPoints = OrderUtils.calBonusPoints(totalAmountDiscount.subtract(lvOrderSaved.getAmountDiscount()));
            mvCustomerRepository.updateBonusPoint(lvCustomer.getId(), bonusPoints);
        }

        //Sau khi đã lưu đơn hàng thì xóa all items
        mvCartItemsService.deleteAllItems(lvCartId);

        //Log
        mvSystemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_ORD_C, MasterObject.Order, "Thêm mới đơn hàng", lvOrderSaved.getCode());
        LOG.info("Insert new order success! insertBy={}", mvUserSession.getUserPrincipal().getUsername());

        return OrderDTO.fromOrder(lvOrderSaved);
    }

    private VldModel vldBeforeUpdateOrder(UpdateOrderReq pOrderRequest) {
        if (!CoreUtils.validateEmail(pOrderRequest.getRecipientEmail()))
            throw new BadRequestException("Email invalid!");
        if (!CoreUtils.validatePhoneNumber(pOrderRequest.getRecipientPhone(), CommonUtils.defaultCountryCode))
            throw new BadRequestException("Phone number invalid!");
        if (CoreUtils.isNullStr(pOrderRequest.getShippingAddress()))
            throw new BadRequestException("Address must not empty!");

        return new VldModel();
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
        OrderDTO lvOrder = this.findById(id, true);
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
        String year = String.valueOf(currentDate.getYear());
        String month = String.format("%02d", currentDate.getMonthValue());
        String day = String.format("%02d", currentDate.getDayOfMonth());
        return year + month + day + String.format("%03d", orderTodayQty + 1);
    }

    private OrderStatus getDefaultOrderStatus() {
        return OrderStatus.PEND;
    }

    public boolean isWithinReturnPeriod(LocalDateTime successfulDeliveryTime, LocalDateTime currentDay, int periodDays) {
        long daysBetween = ChronoUnit.DAYS.between(successfulDeliveryTime, currentDay);
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