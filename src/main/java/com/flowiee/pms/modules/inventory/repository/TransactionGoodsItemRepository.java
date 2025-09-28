package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.inventory.entity.TransactionGoodsItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionGoodsItemRepository extends BaseRepository<TransactionGoodsItem, Long> {
    @Query("from TransactionGoodsItem i where i.transactionGoods.id = :tranId")
    List<TransactionGoodsItem> findByTranId(@Param("tranId") Long pTranId);

    @Query("from TransactionGoodsItem i where i.transactionGoods.id = :tranId and i.productVariant.id = :variantId")
    TransactionGoodsItem findByTranIdAndProductVariantId(@Param("tranId") Long tranId, @Param("variantId") Long variantId);

    @Query("select case when count(i) > 0 then true else false end " +
           "from TransactionGoodsItem i " +
           "where i.transactionGoods.id = :tranId and i.productVariant.id = :variantId")
    boolean existsByTranIdAndProductVariantId(@Param("tranId") Long tranId,
                                              @Param("variantId") Long variantId);

    @Modifying
    @Query("delete from TransactionGoodsItem i where i.transactionGoods.id = :tranId")
    void deleteByTranId(@Param("tranId") Long tranId);
}