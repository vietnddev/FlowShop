package com.flowiee.pms.order.service;

import com.flowiee.pms.order.dto.OrderReturnDTO;
import com.flowiee.pms.order.entity.Order;

import com.flowiee.pms.order.dto.OrderDTO;
import com.flowiee.pms.order.model.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    //1. Basic CRUD
    Page<OrderDTO> find(OrderReq pOrderReq);

    Order findEntById(Long orderId, boolean throwException);

    OrderDTO findDtoById(Long orderId, boolean throwException);

    OrderDTO createOrder(CreateOrderReq pRequest);

    OrderDTO updateOrder(UpdateOrderReq pRequest, Long pOrderId);

    String deleteOrder(Long pOrderId);

    // 2. CHANGE STATUS (Unified behaviour)
    void changeStatus(Long pOrderId, ChangeOrderStatusReq pRequest);

    // 3. QUERY HELPERS
    OrderDTO findByTrackingCode(String pTrackingCode);

    List<OrderReturnDTO> findReturnedOrders();
}