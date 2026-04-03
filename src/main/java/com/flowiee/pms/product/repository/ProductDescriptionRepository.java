package com.flowiee.pms.product.repository;

import com.flowiee.pms.shared.base.BaseRepository;
import com.flowiee.pms.product.entity.ProductDescription;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDescriptionRepository extends BaseRepository<ProductDescription, Long> {
    @Query("from ProductDescription where productId = :productId")
    ProductDescription findByProductId(@Param("productId") Long productId);
}