package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.entity.sales.CustomerDebt;
import com.flowiee.pms.entity.sales.LedgerTransaction;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.repository.category.CategoryRepository;
import com.flowiee.pms.repository.sales.CustomerDebtRepository;
import com.flowiee.pms.repository.sales.OrderRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.sales.LedgerReceiptService;
import com.flowiee.pms.service.sales.OrderPayService;
import com.flowiee.pms.service.sales.OrderReadService;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.utils.OrderUtils;
import com.flowiee.pms.common.enumeration.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderPayServiceImpl extends BaseService implements OrderPayService {
    private final CustomerDebtRepository customerDebtRepository;
    private final LedgerReceiptService mvLedgerReceiptService;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final OrderReadService orderReadService;

    @Transactional
    @Override
    public String doPay(Long orderId, LocalDateTime paymentTime, Long paymentMethod, BigDecimal paymentAmount, String paymentNote) {
        Order lvOrder = orderReadService.findById(orderId, true);

        validate(lvOrder, paymentMethod, paymentAmount);

        if (paymentTime == null) paymentTime = LocalDateTime.now();
        orderRepository.updatePaymentStatus(orderId, paymentTime, paymentMethod, paymentAmount, paymentNote);

        BigDecimal lvOrderValue = OrderUtils.calTotalAmount(lvOrder.getListOrderDetail(), lvOrder.getAmountDiscount());
        if (paymentAmount.compareTo(lvOrderValue) < 0) {
            customerDebtRepository.save(CustomerDebt.builder()
                    .customer(lvOrder.getCustomer())
                    .order(new Order(lvOrder.getId()))
                    .debtAmount(lvOrderValue.subtract(paymentAmount))
                    .dueDate(LocalDate.now())
                    .status("-")
                    .note("Thanh toán chưa đủ cho đơn hàng " + lvOrder.getCode())
                    .build());
        }

        logger.info("Begin generate receipt issued when completed an order");
        BigDecimal lvOrderAmount = OrderUtils.calTotalAmount(lvOrder.getListOrderDetail(), lvOrder.getAmountDiscount());
        Category groupObject = categoryRepository.findByTypeAndCode(CategoryType.GROUP_OBJECT.name(), "KH");//Customer
        Category receiptType = categoryRepository.findByTypeAndCode(CategoryType.RECEIPT_TYPE.name(), "PO");//Payment for order
        mvLedgerReceiptService.save(LedgerTransaction.builder()
                .tranType(LedgerTranType.PT.name())
                .groupObject(groupObject)
                .tranContent(receiptType)
                .paymentMethod(lvOrder.getPaymentMethod())
                .fromToName(lvOrder.getCustomer().getCustomerName())
                .amount(lvOrderAmount)
                .build());
        logger.info("End generate receipt issued when completed an order");

        systemLogService.writeLogUpdate(MODULE.SALES, ACTION.PRO_ORD_U, MasterObject.Order, "Cập nhật trạng thái thanh toán đơn hàng", "Số tiền: " + CommonUtils.formatToVND(paymentAmount));

        return MessageCode.UPDATE_SUCCESS.getDescription();
    }

    private void validate(Order pOrderInfo, Long pPaymentMethod, BigDecimal pPaymentAmount) {
        if (Boolean.TRUE.equals(pOrderInfo.getPaymentStatus()))
            throw new BadRequestException("The order has been paid");

        categoryRepository.findById(pPaymentMethod)
                .orElseThrow(() -> new BadRequestException("Thông tin thanh toán không hợp lệ!"));

        if (pPaymentAmount == null || pPaymentAmount.equals(BigDecimal.ZERO))
            throw new BadRequestException("Thông tin thanh toán không hợp lệ!");
    }
}