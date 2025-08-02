package com.flowiee.pms.modules.sales.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.sales.entity.OrderCart;

import java.util.List;

@Repository
public interface OrderCartRepository extends BaseRepository<OrderCart, Long> {
    //@Query("from OrderCart c where c.createdBy=:createdBy")
    @Query("select distinct c from OrderCart c left join fetch c.listItems where c.createdBy = :createdBy and (c.isFinish <> true)")
    List<OrderCart> findByAccountId(@Param("createdBy") Long createdBy);
}