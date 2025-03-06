package com.flowiee.pms.repository.product;

import com.flowiee.pms.entity.product.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    @Query("from ProductPrice pp " +
           "where pp.state = 'A' " +
           "    and (:productBaseId is null or pp.productBase.id = :productBaseId) " +
           "    and (:productVariantId is null or pp.productVariant.id = :productVariantId) ")
    ProductPrice findPricePresent(@Param("productBaseId") Long productBaseId, @Param("productVariantId") Long productVariantId);

    @Query("from ProductPrice pp " +
           "where 1=1 " +
           "    and (:productBaseId is null or pp.productBase.id = :productBaseId) " +
           "    and (:productVariantId is null or pp.productVariant.id = :productVariantId) ")
    List<ProductPrice> findPrices(@Param("productBaseId") Long productBaseId, @Param("productVariantId") Long productVariantId);

    @Query("from ProductPrice p " +
           "where p.state = 'A' " +
           "    and (coalesce(:productVariantIds, -1) = -1 or p.productVariant.id in :productVariantIds) ")
    List<ProductPrice> findPresentPrices(@Param("productVariantIds") List<Long> productVariantIds);
}