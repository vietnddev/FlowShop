package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.inventory.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends BaseRepository<Product, Long> {
    @Query("select p.id, p.productName from Product p")
    List<Object[]> findIdAndName();

    @Query("select coalesce(sum(pd.storageQty),0) from ProductDetail pd where pd.product.id = :productId")
    int getStockQty(@Param("productId") Long productId);

    @Query("select coalesce(sum(pd.soldQty),0) from ProductDetail pd where pd.product.id = :productId")
    int getSoldQty(@Param("productId") Long productId);

    @Query("select coalesce(sum(pd.defectiveQty),0) from ProductDetail pd where pd.product.id = :productId")
    int getDefectiveQty(@Param("productId") Long productId);

    @Query("select count(pd) > 0 from ProductDetail pd where pd.product.id = :productId and pd.status = 'ACT'")
    boolean hasActiveStatus(@Param("productId") Long productId);
}