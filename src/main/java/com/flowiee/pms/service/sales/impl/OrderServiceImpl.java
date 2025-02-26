package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.entity.sales.*;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.entity.system.Account;
import com.flowiee.pms.entity.system.SystemConfig;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.model.payload.CreateOrderReq;
import com.flowiee.pms.model.payload.UpdateOrderReq;
import com.flowiee.pms.repository.sales.CustomerRepository;
import com.flowiee.pms.repository.system.ConfigRepository;
import com.flowiee.pms.security.UserSession;
import com.flowiee.pms.service.category.CategoryService;
import com.flowiee.pms.service.system.AccountService;
import com.flowiee.pms.service.system.SendCustomerNotificationService;
import com.flowiee.pms.common.ChangeLog;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.model.dto.OrderDTO;
import com.flowiee.pms.exception.DataInUseException;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.sales.*;
import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.repository.sales.OrderRepository;

import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.OrderUtils;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends BaseService implements OrderReadService, OrderWriteService {
    private final SendCustomerNotificationService mvSendCustomerNotificationService;
    private final CartService           mvCartService;
    private final OrderRepository       mvOrderRepository;
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
    private final CustomerService       mvCustomerService;
    private final AccountService        mvAccountService;
    private final UserSession           mvUserSession;

    private BigDecimal mvDefaultShippingCost = BigDecimal.ZERO;
    private BigDecimal mvDefaultPackagingCost = BigDecimal.ZERO;
    private BigDecimal mvDefaultGiftWrapCost = BigDecimal.ZERO;
    private BigDecimal mvDefaultCodFee = BigDecimal.ZERO;

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
        addEqualCondition(lvCriteriaBuilder, lvPredicates, lvRoot.get("kenhBanHang").get("id"), pSalesChannelId);
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
    public Order findById(Long pOrderId, boolean throwException) {
        Optional<Order> lvOrderOptional = mvOrderRepository.findById(pOrderId);
        if (lvOrderOptional.isPresent()) {
            return lvOrderOptional.get();
        }
        if (throwException) {
            throw new EntityNotFoundException(new Object[] {"order"}, null, null);
        }
        return null;
    }

    @Override
    public Order findByTrackingCode(String pTrackingCode) {
        return mvOrderRepository.findByTrackingCode(pTrackingCode);
    }

    private VldModel vldBeforeCreateOrder(CreateOrderReq pOrderRequest, Long pCartId, String pVoucherCode, Long pPaymentMethodId, Long pSaleChannelId, Long pCustomerId, Long pSalesAssistantId) {
        OrderCart lvCart = mvCartService.findById(pCartId, true);
        if (ObjectUtils.isEmpty(lvCart.getListItems()))
            throw new BadRequestException("At least one product in the order!");

        Category lvPaymentMethod = mvCategoryService.findById(pPaymentMethodId, false);
        if (lvPaymentMethod == null || !lvPaymentMethod.getStatus())
            throw new BadRequestException("Payment method invalid!");
        Category lvSalesChannel = mvCategoryService.findById(pSaleChannelId, false);
        if (lvSalesChannel == null || !lvSalesChannel.getStatus())
            throw new BadRequestException("Sales channel invalid!");

        Customer lvCustomer = mvCustomerService.findById(pCustomerId, true);
        if (lvCustomer.getIsBlackList())
            throw new BadRequestException("The customer is on the blacklist!");
        if (!CoreUtils.validateEmail(pOrderRequest.getRecipientEmail()))
            throw new BadRequestException("Email invalid!");
        if (!CoreUtils.validatePhoneNumber(pOrderRequest.getRecipientPhone(), CommonUtils.defaultCountryCode))
            throw new BadRequestException("Phone number invalid!");
        if (CoreUtils.isNullStr(pOrderRequest.getShippingAddress()))
            throw new BadRequestException("Address must not empty!");

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

        BigDecimal lvAmountDiscount = request.getAmountDiscount();
        String lvVoucherUsedCode = request.getVoucherUsed();
        LocalDateTime lvOrderTime = LocalDateTime.parse(request.getOrderTime(), formatter);
        Long lvCartId = request.getCartId();

        VldModel vldModel = vldBeforeCreateOrder(request, lvCartId, lvVoucherUsedCode, request.getPaymentMethodId(),
                request.getSalesChannelId(), request.getCustomerId(), request.getSalesAssistantId());
        OrderCart lvCart = vldModel.getOrderCart();
        VoucherTicket lvVoucherTicket = vldModel.getVoucherTicket();
        Customer lvCustomer = vldModel.getCustomer();

        Order order = Order.builder()
                .code(getNextOrderCode())
                .customer(lvCustomer)
                .kenhBanHang(vldModel.getSalesChannel())
                .nhanVienBanHang(vldModel.getSalesAssistant())
                .note(request.getNote())
                .orderTime(lvOrderTime != null ? lvOrderTime : LocalDateTime.now())
                .receiverName(request.getRecipientName())
                .receiverPhone(request.getRecipientPhone())
                .receiverEmail(request.getRecipientEmail())
                .receiverAddress(request.getShippingAddress())
                .paymentMethod(vldModel.getPaymentMethod())
                .paymentStatus(false)
                .voucherUsedCode(ObjectUtils.isNotEmpty(lvVoucherUsedCode) ? lvVoucherUsedCode : null)
                .amountDiscount(CoreUtils.coalesce(lvAmountDiscount))
                .packagingCost(CoreUtils.coalesce(request.getPackagingCost(), mvDefaultPackagingCost))
                .shippingCost(CoreUtils.coalesce(request.getShippingCost(), mvDefaultShippingCost))
                .giftWrapCost(CoreUtils.coalesce(request.getGiftWrapCost(), mvDefaultGiftWrapCost))
                .codFee(CoreUtils.coalesce(request.getCodFee(), mvDefaultCodFee))
                .isGiftWrapped(false)
                .orderStatus(getDefaultOrderStatus())
                .build();
        order.setPriorityLevel(determinePriority(order));

        if (lvVoucherTicket != null) {
            order.setVoucherUsedCode(lvVoucherTicket.getCode());
            //Update voucher ticket's status to used
            lvVoucherTicket.setCustomer(lvCustomer);
            lvVoucherTicket.setActiveTime(new Date());
            lvVoucherTicket.setUsed(true);
            mvVoucherTicketService.update(lvVoucherTicket, lvVoucherTicket.getId());
        }

        //Create order
        Order lvOrderSaved = mvOrderRepository.save(order);
        //Create QRCode
        try {
            mvOrderGenerateQRCodeService.generateOrderQRCode(lvOrderSaved.getId());
        } catch (IOException | WriterException e ) {
            e.printStackTrace();
            logger.error(String.format("Can't generate QR Code for Order %s", lvOrderSaved.getCode()), e);
        }
        //Create items detail
        List<OrderDetail> lvOrderItemsList = mvOrderItemsService.save(lvCart.getId(), lvOrderSaved.getId(), lvCart.getListItems());
        BigDecimal totalAmountDiscount = OrderUtils.calTotalAmount(lvOrderItemsList, lvOrderSaved.getAmountDiscount());
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
        systemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_ORD_C, MasterObject.Order, "Thêm mới đơn hàng", lvOrderSaved.getCode());
        logger.info("Insert new order success! insertBy={}", mvUserSession.getUserPrincipal().getUsername());

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

    private Order beforeUpdateOrder(Order pOrder) {
        OrderStatus lvOrderStatus = pOrder.getOrderStatus();
        LocalDateTime lvSuccessfulDeliveryTime = pOrder.getSuccessfulDeliveryTime();

        switch (lvOrderStatus) {
            case RTND:
                SystemConfig lvReturnPeriodDaysMdl = mvConfigRepository.findByCode(ConfigCode.returnPeriodDays.name());
                if (SysConfigUtils.isValid(lvReturnPeriodDaysMdl)) {
                    throw new AppException("System has not configured the time allowed to return the order!");
                }
                int lvReturnPeriodDays = lvReturnPeriodDaysMdl.getIntValue();
                if (!isWithinReturnPeriod(lvSuccessfulDeliveryTime, LocalDateTime.now(), lvReturnPeriodDays)) {
                    throw new AppException("The return period has expired!");
                }
                break;
        }

        return ObjectUtils.clone(pOrder);
    }

    @Transactional
    @Override
    public OrderDTO updateOrder(UpdateOrderReq request, Long pOrderId) {
        Order orderToUpdate = mvOrderRepository.findById(pOrderId)
                .orElseThrow(() -> new AppException("Order not found!"));
        LocalDateTime lvSuccessfulDeliveryTime = request.getSuccessfulDeliveryTime();

        //Validate request data
        vldBeforeUpdateOrder(request);
        //Validate business before update
        Order orderBefore = beforeUpdateOrder(orderToUpdate);

        //Update new info
        orderToUpdate.setReceiverName(request.getRecipientName());
        orderToUpdate.setReceiverPhone(request.getRecipientPhone());
        orderToUpdate.setReceiverEmail(request.getRecipientEmail());
        orderToUpdate.setReceiverAddress(request.getShippingAddress());
        orderToUpdate.setNote(request.getNote());
        Order orderUpdated = mvOrderRepository.save(orderToUpdate);

        //Do something after updated
        afterUpdatedOrder(orderUpdated, null);

        //Log
        ChangeLog changeLog = new ChangeLog(orderBefore, orderUpdated);
        mvOrderHistoryService.save(changeLog.getLogChanges(), "Cập nhật đơn hàng", pOrderId, null);
        systemLogService.writeLogUpdate(MODULE.SALES, ACTION.PRO_ORD_U, MasterObject.Order, "Cập nhật đơn hàng", changeLog);
        logger.info("Cập nhật đơn hàng {}", orderUpdated.toString());

        return OrderDTO.fromOrder(orderToUpdate);
    }

    private void afterUpdatedOrder(Order pOrderUpdated, Long pLoyaltyProgramId) {
        String lvOrderCode = pOrderUpdated.getCode();
        OrderStatus lvOrderStatus = pOrderUpdated.getOrderStatus();
        Long lvStorageId = pOrderUpdated.getTicketExport().getStorage().getId();
        boolean isNeedRefund = false;

        switch (lvOrderStatus) {
            case CNCL:
                if (SysConfigUtils.isYesOption(ConfigCode.sendNotifyCustomerOnOrderConfirmation)) {
                    mvSendCustomerNotificationService.notifyOrderConfirmation(pOrderUpdated, pOrderUpdated.getReceiverEmail());
                }
                break;
            case RTND:
                mvTicketImportService.restockReturnedItems(lvStorageId, lvOrderCode);
                if (isNeedRefund) {
                    //create ledger transaction record for export
                }
                break;
            case DLVD:
                mvLoyaltyProgramService.accumulatePoints(pOrderUpdated, pLoyaltyProgramId);
                break;
        }
    }

    @Override
    public String deleteOrder(Long id) {
        Order lvOrder = this.findById(id, true);
        if (lvOrder.getPaymentStatus()) {
            throw new DataInUseException(ErrorCode.ERROR_DATA_LOCKED.getDescription());
        }
        mvOrderRepository.deleteById(id);
        systemLogService.writeLogDelete(MODULE.PRODUCT, ACTION.PRO_ORD_D, MasterObject.Order, "Xóa đơn hàng", lvOrder.toString());
        logger.info("Xóa đơn hàng orderId={}", id);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public List<Order> findOrdersToday() {
        return mvOrderRepository.findOrdersToday();
    }

    @Override
    public Page<OrderDTO> getOrdersByCustomer(int pageSize, int pageNum, Long pCustomerId) {
        return this.findAll(pageSize, pageNum, null, null, null, null, null, null, pCustomerId, null, null, null, null, null, null);
    }

    @Override
    public String updateOrderStatus(Long pOrderId, OrderStatus pOrderStatus, LocalDateTime pSuccessfulDeliveryTime, Long cancellationReasonId) {
        Order lvOrder = this.findById(pOrderId, true);
        if (lvOrder.getOrderStatus().equals(pOrderStatus)) {
            throw new BadRequestException(String.format("Order status is %s now!", pOrderStatus.getName()));
        }

        lvOrder.setOrderStatus(pOrderStatus);
        if (pOrderStatus.equals(OrderStatus.DLVD))
            lvOrder.setSuccessfulDeliveryTime(pSuccessfulDeliveryTime != null ? pSuccessfulDeliveryTime : LocalDateTime.now());
        if (pOrderStatus.equals(OrderStatus.CNCL)) {
            lvOrder.setCancellationReason(cancellationReasonId);
            lvOrder.setCancellationDate(LocalDateTime.now());
        }

        mvOrderRepository.save(lvOrder);
        return "Updated successfully!";
    }

    @Override
    public Order getOrderByCode(String pOrderCode) {
        return mvOrderRepository.findByOrderCode(pOrderCode);
    }

    private String getNextOrderCode() {
        int orderTodayQty = 0;
        List<Order> ordersToday = mvOrderRepository.findOrdersToday();
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
}