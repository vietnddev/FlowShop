package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.enumeration.TransactionGoodsType;
import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.sales.dto.OrderDTO;
import com.flowiee.pms.modules.inventory.dto.TransactionGoodsDTO;
import com.flowiee.pms.modules.sales.repository.OrderRepository;
import com.flowiee.pms.modules.system.repository.ConfigRepository;
import com.flowiee.pms.modules.promotion.service.LoyaltyProgramService;
import com.flowiee.pms.modules.inventory.service.TicketImportService;
import com.flowiee.pms.modules.inventory.service.TransactionGoodsService;
import com.flowiee.pms.modules.system.service.SendCustomerNotificationService;
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
    public void cancelOrder(OrderDTO pOrder, String pReason) {
        Order lvOrder = orderRepository.findById(pOrder.getId()).orElseThrow(() -> new BadRequestException());
        lvOrder.setCancellationReason(null);
        lvOrder.setCancellationDate(LocalDateTime.now());
        Order lvOrderUpdated = orderRepository.save(lvOrder);

        if (SysConfigUtils.isYesOption(ConfigCode.sendNotifyCustomerOnOrderConfirmation)) {
            sendCustomerNotificationService.notifyOrderConfirmation(lvOrderUpdated, lvOrderUpdated.getReceiverEmail());
        }

        //...
    }

    @Override
    public void completeOrder(OrderDTO pOrder) {
        Order lvOrder = orderRepository.findById(pOrder.getId()).orElseThrow(() -> new BadRequestException());
        lvOrder.setOrderStatus(OrderStatus.DLVD);
        lvOrder.setDeliverySuccessTime(LocalDateTime.now());
        Order lvOrderUpdated = orderRepository.save(lvOrder);

        Long lvProgramId = null;
        loyaltyProgramService.accumulatePoints(lvOrderUpdated, lvProgramId);

        //...
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void returnOrder(OrderDTO pOrder) {
        try {
            Order lvOrder = orderRepository.findById(pOrder.getId()).orElseThrow(() -> new BadRequestException());
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
        Order lvOrder = orderRepository.findById(pOrderId).orElseThrow(() -> new BadRequestException());
        //...
    }
}