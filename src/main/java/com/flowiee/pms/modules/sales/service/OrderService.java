package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.modules.sales.entity.Order;

import com.flowiee.pms.common.enumeration.OrderStatus;
import com.flowiee.pms.modules.sales.dto.OrderDTO;
import com.flowiee.pms.modules.sales.model.CreateOrderReq;
import com.flowiee.pms.modules.sales.model.OrderReq;
import com.flowiee.pms.modules.sales.model.UpdateOrderReq;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    Page<OrderDTO> find(OrderReq pOrderReq);

    Order findEntById(Long orderId, boolean throwException);

    OrderDTO findDtoById(Long orderId, boolean throwException);

    OrderDTO findByTrackingCode(String pTrackingCode);

    Order findByCode(String pOrderCode);

    List<Order> findOrdersToday();

    String updateOrderStatus(Long pOrderId, OrderStatus pOrderStatus, LocalDateTime pSuccessfulDeliveryTime, Long cancellationReasonId);

    OrderDTO createOrder(CreateOrderReq pRequest);

    OrderDTO updateOrder(UpdateOrderReq pRequest, Long pOrderId);

    String deleteOrder(Long pOrderId);

    void doCancel(OrderDTO pOrder, String pReason);

    void doComplete(OrderDTO pOrder);

    void doReturn(OrderDTO pOrder);

    void doRefund(Long pOrderId);
}