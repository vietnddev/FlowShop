package com.flowiee.pms.product.repository;

import com.flowiee.pms.product.dto.ProductPriceDTO;
import com.flowiee.pms.shared.base.BaseRepository;
import com.flowiee.pms.product.entity.ProductPrice;
import com.flowiee.pms.product.enums.PriceType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductPriceRepository extends BaseRepository<ProductPrice, Long> {
    @Query("from ProductPrice pp " +
            "where pp.state = 'A' and (:productVariantId is null or pp.productVariant.id = :productVariantId)")
    List<ProductPrice> findByVariantId(@Param("productVariantId") Long productVariantId);

    @Query("""
        select new com.flowiee.pms.product.dto.ProductPriceDTO(
            pp.productVariant.id,
            max(case when pp.priceType = com.flowiee.pms.product.enums.PriceType.RTL then pp.priceValue else null end),
            max(case when pp.priceType = com.flowiee.pms.product.enums.PriceType.WHO then pp.priceValue else null end),
            max(case when pp.priceType = com.flowiee.pms.product.enums.PriceType.CSP then pp.priceValue else null end))
        from ProductPrice pp
        where pp.state = 'A'
            and (:productVariantIds is null or pp.productVariant.id in :productVariantIds)
        group by pp.productVariant.id
    """)
    List<ProductPriceDTO> findPricesByVariantIds(@Param("productVariantIds") List<Long> productVariantIds);

    @Modifying
    @Query("update ProductPrice pp set pp.state = 'I' where pp.id = :priceId")
    void inactivePrice(@Param("priceId") Long priceId);

    @Query("select count(p) > 0 " +
           "from ProductPrice p " +
           "where p.state = :state " +
           "    and p.priceType = :type " +
           "    and p.productVariant.id = :productVariantId")
    boolean existsByStateAndTypeAndProductVariantId(@Param("state") String state,
                                                    @Param("type") PriceType type,
                                                    @Param("productVariantId") Long productVariantId);
}