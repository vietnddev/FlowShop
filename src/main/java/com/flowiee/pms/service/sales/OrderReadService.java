package com.flowiee.pms.service.sales;

import com.flowiee.pms.entity.sales.Order;

import com.flowiee.pms.common.enumeration.OrderStatus;
import com.flowiee.pms.model.dto.OrderDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderReadService {
    List<OrderDTO> findAll();

    Page<OrderDTO> findAll(int pageSize, int pageNum, String pTxtSearch, Long pOrderId, Long pPaymentMethodId,
                           OrderStatus pOrderStatus, Long pSalesChannelId, Long pSellerId, Long pCustomerId,
                           Long pBranchId, Long pGroupCustomerId, String pDateFilter, LocalDateTime pOrderTimeFrom, LocalDateTime pOrderTimeTo, String pSortBy);

    OrderDTO findById(Long orderId, boolean throwException);

    OrderDTO findByTrackingCode(String pTrackingCode);

    List<Order> findOrdersToday();

    Page<OrderDTO> getOrdersByCustomer(int pageSize, int pageNum, Long pCustomerId);

    String updateOrderStatus(Long pOrderId, OrderStatus pOrderStatus, LocalDateTime pSuccessfulDeliveryTime, Long cancellationReasonId);

    Order getOrderByCode(String pOrderCode);
}