package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.inventory.entity.ProductHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductHistoryRepository extends BaseRepository<ProductHistory, Long> {
    @Query("from ProductHistory p where p.product.id=:productId")
    List<ProductHistory> findByProductId(@Param("productId") Long productId);

    @Query("from ProductHistory p where p.productDetail.id=:productVariantId and p.field=:field order by p.id desc")
    List<ProductHistory> findHistoryChangeOfProductDetail(@Param("productVariantId") Long productVariantId, @Param("field") String field);
}