package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.inventory.model.ProductSummaryModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.inventory.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends BaseRepository<Product, Long> {
    @Query("select p.id, p.productName from Product p")
    List<Object[]> findIdAndName();

    @Query("""
        select new com.flowiee.pms.modules.inventory.model.ProductSummaryModel(
            pd.product.id,
            cast(coalesce(sum(pd.storageQty),0) as integer),
            cast(coalesce(sum(pd.soldQty),0) as integer),
            cast(coalesce(sum(pd.defectiveQty),0) as integer))
        from com.flowiee.pms.modules.inventory.entity.ProductDetail pd
        where pd.product.id = :productId and pd.deletedAt is null
        group by pd.product.id
    """)
    ProductSummaryModel getSummariesQty(@Param("productId") Long productId);

    @Query("select count(pd) > 0 from ProductDetail pd where pd.product.id = :productId and pd.status = 'ACT'")
    boolean hasActiveStatus(@Param("productId") Long productId);
}