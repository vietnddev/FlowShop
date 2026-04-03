package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.shared.base.BaseRepository;
import com.flowiee.pms.modules.inventory.entity.ProductPriceHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductPriceHistoryRepository extends BaseRepository<ProductPriceHistory, Long> {
    @Query("""
        select pph from ProductPriceHistory pph
        inner join ProductPrice pp on pp.id = pph.productPrice.id
        inner join ProductDetail pd on pd.id = pp.productVariant.id
        where pd.product.id = :productId
        order by pph.changeTime desc
    """)
    List<ProductPriceHistory> findByProductId(Long productId);
}