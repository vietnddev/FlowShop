package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.inventory.entity.ProductPrice;
import com.flowiee.pms.modules.inventory.enums.PriceType;
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

    @Query("select count(p) > 0 " +
           "from ProductPrice p " +
           "where p.state = :state " +
           "    and p.priceType = :type " +
           "    and p.productVariant.id = :productVariantId")
    boolean existsByStateAndTypeAndProductVariantId(@Param("state") String state,
                                                    @Param("type") PriceType type,
                                                    @Param("productVariantId") Long productVariantId);
}