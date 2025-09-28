package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.inventory.entity.TransactionGoods;
import com.flowiee.pms.modules.inventory.enums.TransactionGoodsType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionGoodsRepository extends BaseRepository<TransactionGoods, Long> {
    @Query("from TransactionGoods t " +
           "where (:tranType is null or t.transactionType = :tranType) " +
           "    and (:warehouseId is null or t.warehouse.id = :warehouseId) " +
           "order by t.id desc ")
    Page<TransactionGoods> findAll(@Param("tranType") TransactionGoodsType tranType,
                                   @Param("warehouseId") Long warehouseId, Pageable pageable);
    TransactionGoods findTopByTransactionTypeOrderByIdDesc(TransactionGoodsType transactionType);
}