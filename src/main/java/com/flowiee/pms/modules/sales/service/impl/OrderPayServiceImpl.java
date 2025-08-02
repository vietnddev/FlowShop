package com.flowiee.pms.modules.sales.service.impl;

import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.modules.sales.service.LedgerReceiptService;
import com.flowiee.pms.modules.sales.service.OrderPayService;
import com.flowiee.pms.modules.sales.service.OrderService;
import com.flowiee.pms.modules.system.dto.CategoryDTO;
import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.modules.sales.entity.CustomerDebt;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.sales.dto.LedgerTransactionDTO;
import com.flowiee.pms.modules.system.repository.CategoryRepository;
import com.flowiee.pms.modules.sales.repository.CustomerDebtRepository;
import com.flowiee.pms.modules.sales.repository.OrderRepository;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.utils.OrderUtils;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.modules.system.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OrderPayServiceImpl implements OrderPayService {
    private final CustomerDebtRepository customerDebtRepository;
    private final LedgerReceiptService mvLedgerReceiptService;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final OrderService mvOrderService;
    private final ModelMapper modelMapper;
    private final SystemLogService systemLogService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional
    @Override
    public String doPay(Long orderId, String pPaymentTime, Long paymentMethod, BigDecimal paymentAmount, String paymentNote) {
        Order lvOrder = orderRepository.findById(orderId).orElseThrow(BadRequestException::new);

        validate(lvOrder, paymentMethod, paymentAmount);

        LocalDateTime lvPaymentTime = getPaymentTime(pPaymentTime);
        orderRepository.updatePaymentStatus(orderId, lvPaymentTime, paymentMethod, paymentAmount, paymentNote);

        BigDecimal lvOrderValue = OrderUtils.calAmount(lvOrder.getListOrderDetail(), lvOrder.getAmountDiscount());
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
        BigDecimal lvOrderAmount = OrderUtils.calAmount(lvOrder.getListOrderDetail(), lvOrder.getAmountDiscount());
        Category groupObject = categoryRepository.findByTypeAndCode(CATEGORY.GROUP_OBJECT.name(), "KH");//Customer
        Category receiptType = categoryRepository.findByTypeAndCode(CATEGORY.RECEIPT_TYPE.name(), "6");//Payment for order
//        LedgerTransactionDTO lvLedgerTransactionDTO = modelMapper.map(LedgerTransaction.builder()
//                .tranType(LedgerTranType.PT.name())
//                .groupObject(groupObject)
//                .tranContent(receiptType)
//                .paymentMethod(lvOrder.getPaymentMethod())
//                .fromToName(lvOrder.getCustomer().getCustomerName())
//                .amount(lvOrderAmount)
//                .build(),
//                LedgerTransactionDTO.class);

        LedgerTransactionDTO lvLedgerTransactionDTO = new LedgerTransactionDTO();
        lvLedgerTransactionDTO.setTranType(LedgerTranType.PT.name());
        lvLedgerTransactionDTO.setGroupObject(new CategoryDTO(groupObject.getId(), groupObject.getName()));
        lvLedgerTransactionDTO.setTranContent(new CategoryDTO(receiptType.getId(), receiptType.getName()));
        lvLedgerTransactionDTO.setPaymentMethod(new CategoryDTO(lvOrder.getPaymentMethod().getId(), lvOrder.getPaymentMethod().getName()));
        lvLedgerTransactionDTO.setFromToName(lvOrder.getCustomer().getCustomerName());
        lvLedgerTransactionDTO.setAmount(lvOrderAmount);


        mvLedgerReceiptService.save(lvLedgerTransactionDTO);
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

    private LocalDateTime getPaymentTime(String pPaymentTime) {
        if (CoreUtils.isNullStr(pPaymentTime)) {
            return LocalDateTime.now();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");
        DateTimeFormatter formatterUS = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);
        LocalDateTime lvPaymentTime = null;

        try {
            lvPaymentTime = LocalDateTime.parse(pPaymentTime, formatter);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            try {
                TemporalAccessor temporalAccessor = formatterUS.parse(pPaymentTime);
                lvPaymentTime = LocalDateTime.from(temporalAccessor);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return lvPaymentTime;
    }
}