package com.flowiee.pms.modules.sales.repository;

import com.flowiee.pms.modules.sales.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    @Query("from OrderHistory o where o.order.id=:orderId")
    List<OrderHistory> findByOrderId(@Param("orderId") Long orderId);

    @Query("from OrderHistory o where o.orderDetail.id=:orderDetailId")
    List<OrderHistory> findByOrderDetailId(@Param("orderDetailId") Long orderDetailId);
}