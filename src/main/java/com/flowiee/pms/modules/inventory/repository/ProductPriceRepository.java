package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.inventory.entity.ProductPrice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductPriceRepository extends BaseRepository<ProductPrice, Long> {
    @Query("from ProductPrice pp " +
           "where pp.state = 'A' and (:productVariantId is null or pp.productVariant.id = :productVariantId) ")
    ProductPrice findPricePresent(@Param("productVariantId") Long productVariantId);

    @Query("from ProductPrice pp " +
           "where (:productVariantId is null or pp.productVariant.id = :productVariantId) ")
    List<ProductPrice> findPrices(@Param("productVariantId") Long productVariantId);

    @Query("from ProductPrice pp where pp.state = 'A' and (:productVariantId is null or pp.productVariant.id = :productVariantId)")
    List<ProductPrice> findPresentPrices(@Param("productVariantId") Long productVariantId);

    @Query("from ProductPrice p " +
           "where p.state = 'A' " +
           "    and (coalesce(:productVariantIds, -1) = -1 or p.productVariant.id in :productVariantIds) ")
    List<ProductPrice> findPresentPrices(@Param("productVariantIds") List<Long> productVariantIds);
}