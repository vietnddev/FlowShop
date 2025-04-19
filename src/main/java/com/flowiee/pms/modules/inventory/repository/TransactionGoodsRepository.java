package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.inventory.entity.TransactionGoods;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionGoodsRepository extends BaseRepository<TransactionGoods, Long> {
}