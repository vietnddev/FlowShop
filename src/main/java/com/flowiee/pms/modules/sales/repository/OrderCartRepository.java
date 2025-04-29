package com.flowiee.pms.modules.sales.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.sales.entity.OrderCart;

import java.util.List;

@Repository
public interface OrderCartRepository extends JpaRepository<OrderCart, Long> {
    //@Query("from OrderCart c where c.createdBy=:createdBy")
    @Query("select distinct c from OrderCart c left join fetch c.listItems where c.createdBy = :createdBy")
    List<OrderCart> findByAccountId(@Param("createdBy") Long createdBy);
}