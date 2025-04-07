package com.flowiee.pms.repository.storage;

import com.flowiee.pms.base.BaseRepository;
import com.flowiee.pms.entity.storage.TransactionGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionGoodsRepository extends BaseRepository<TransactionGoods, Long> {
}