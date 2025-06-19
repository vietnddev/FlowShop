package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.inventory.entity.ProductDescription;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDescriptionRepository extends BaseRepository<ProductDescription, Long> {
    @Query("from ProductDescription where productId = :productId")
    ProductDescription findByProductId(@Param("productId") Long productId);
}