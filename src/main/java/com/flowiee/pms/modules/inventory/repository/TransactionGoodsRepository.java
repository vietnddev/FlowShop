package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.inventory.entity.TransactionGoods;
import com.flowiee.pms.modules.inventory.enums.TransactionGoodsType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionGoodsRepository extends BaseRepository<TransactionGoods, Long> {
    @Query("from TransactionGoods t where t.transactionType = :transactionType order by t.id desc")
    TransactionGoods findLatestByType(@Param("transactionType") TransactionGoodsType transactionType);
}