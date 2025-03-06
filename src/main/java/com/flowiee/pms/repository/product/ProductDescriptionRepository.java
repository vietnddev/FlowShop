package com.flowiee.pms.repository.product;

import com.flowiee.pms.entity.product.ProductDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDescriptionRepository extends JpaRepository<ProductDescription, Long> {
    @Query("from ProductDescription where productId = :productId")
    ProductDescription findByProductId(@Param("productId") Long productId);
}