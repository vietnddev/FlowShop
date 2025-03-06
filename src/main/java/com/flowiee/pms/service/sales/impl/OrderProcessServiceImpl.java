package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.common.enumeration.TransactionGoodsType;
import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.model.dto.TransactionGoodsDTO;
import com.flowiee.pms.repository.sales.OrderRepository;
import com.flowiee.pms.repository.system.ConfigRepository;
import com.flowiee.pms.service.sales.LoyaltyProgramService;
import com.flowiee.pms.service.sales.OrderProcessService;
import com.flowiee.pms.service.sales.OrderReadService;
import com.flowiee.pms.service.sales.TicketImportService;
import com.flowiee.pms.service.storage.TransactionGoodsService;
import com.flowiee.pms.service.system.SendCustomerNotificationService;
import com.flowiee.pms.common.enumeration.ConfigCode;
import com.flowiee.pms.common.enumeration.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderProcessServiceImpl extends BaseService implements OrderProcessService {
    private static final Logger LOG = LoggerFactory.getLogger(OrderProcessServiceImpl.class);

    private final SendCustomerNotificationService sendCustomerNotificationService;
    private final TransactionGoodsService transactionGoodsService;
    private final LoyaltyProgramService loyaltyProgramService;
    private final TicketImportService ticketImportService;
    private final ConfigRepository configRepository;
    private final OrderReadService orderReadService;
    private final OrderRepository orderRepository;

    @Override
    public void cancelOrder(Order pOrder, String pReason) {
        Order lvOrder = pOrder;
        lvOrder.setCancellationReason(null);
        lvOrder.setCancellationDate(LocalDateTime.now());
        Order lvOrderUpdated = orderRepository.save(lvOrder);

        if (SysConfigUtils.isYesOption(ConfigCode.sendNotifyCustomerOnOrderConfirmation)) {
            sendCustomerNotificationService.notifyOrderConfirmation(lvOrderUpdated, lvOrderUpdated.getReceiverEmail());
        }

        //...
    }

    @Override
    public void completeOrder(Order pOrder) {
        Order lvOrder = pOrder;
        lvOrder.setOrderStatus(OrderStatus.DLVD);
        lvOrder.setDeliverySuccessTime(LocalDateTime.now());
        Order lvOrderUpdated = orderRepository.save(lvOrder);

        Long lvProgramId = null;
        loyaltyProgramService.accumulatePoints(lvOrderUpdated, lvProgramId);

        //...
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void returnOrder(Order pOrder) {
        try {
            Order lvOrder = pOrder;
            Long lvStorageId = null;
            String lvOrderCode = lvOrder.getCode();
            ticketImportService.restockReturnedItems(lvStorageId, lvOrderCode);

            TransactionGoodsDTO dto = new TransactionGoodsDTO();
            dto.setId(null);
            dto.setType(TransactionGoodsType.RECEIPT.getValue());
            dto.setDescription(null);

            transactionGoodsService.createTransactionGoods(dto);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Cancel order got [{}]", e.getMessage(), e);
        }
    }

    @Override
    public void refundOrder(Long pOrderId) {
        Order lvOrder = orderReadService.findById(pOrderId, true);

        //...
    }
}