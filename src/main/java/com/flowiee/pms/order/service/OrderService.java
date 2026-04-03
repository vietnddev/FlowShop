package com.flowiee.pms.order.service;

import com.flowiee.pms.order.dto.OrderReturnDTO;
import com.flowiee.pms.order.entity.Order;

import com.flowiee.pms.order.enums.OrderStatus;
import com.flowiee.pms.order.dto.OrderDTO;
import com.flowiee.pms.order.model.CreateOrderReq;
import com.flowiee.pms.order.model.OrderReq;
import com.flowiee.pms.order.model.OrderReturnReq;
import com.flowiee.pms.order.model.UpdateOrderReq;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    Page<OrderDTO> find(OrderReq pOrderReq);

    Order findEntById(Long orderId, boolean throwException);

    OrderDTO findDtoById(Long orderId, boolean throwException);

    OrderDTO findByTrackingCode(String pTrackingCode);

    List<Order> findOrdersToday();

    String updateOrderStatus(Long pOrderId, OrderStatus pOrderStatus, LocalDateTime pSuccessfulDeliveryTime, Long cancellationReasonId);

    OrderDTO createOrder(CreateOrderReq pRequest);

    OrderDTO updateOrder(UpdateOrderReq pRequest, Long pOrderId);

    String deleteOrder(Long pOrderId);

    void doCancel(OrderDTO pOrder, String pReason);

    void doComplete(OrderDTO pOrder);

    void doReturn(OrderReturnReq pOrder) throws Exception;

    List<OrderReturnDTO> findReturnedOrders();

    void doRefund(Long pOrderId);
}