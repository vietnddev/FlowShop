package com.flowiee.pms.modules.sales.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.sales.entity.OrderDetail;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("from OrderDetail d where d.order.id=:orderId")
    List<OrderDetail> findByOrderId(@Param("orderId") Long orderId);

    @Query("from OrderDetail d where d.order.id = :orderId and d.productDetail.id = :productVariantId")
    OrderDetail findByOrderIdAndProductVariantId(@Param("orderId") long orderId, @Param("productVariantId") long productVariantId);

    @Modifying
    @Query("update OrderDetail d set d.isReturned = :isReturned where d.id = :itemId")
    void updateReturnsStatus(@Param("itemId") long itemId, @Param("isReturned") boolean isReturned);
}