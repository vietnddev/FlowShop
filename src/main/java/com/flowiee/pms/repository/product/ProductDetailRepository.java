package com.flowiee.pms.repository.product;

import com.flowiee.pms.entity.product.Product;
import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.model.ProductSummaryInfoModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductDetailRepository extends JpaRepository <ProductDetail, Long>{
    @Query("from ProductDetail b where b.product.id=:productId and b.color.id=:colorId and b.size.id=:sizeId and b.fabricType.id=:fabricTypeId")
    ProductDetail findByColorAndSize(@Param("productId") Long productId, @Param("colorId") Long colorId, @Param("sizeId")  Long sizeId, @Param("fabricTypeId")  Long fabricTypeId);

    @Query("select sum(nvl(p.soldQty, 0)) as totalQtySell from ProductDetail p where p.product.id=:productId")
    Integer findTotalQtySell(@Param("productId") Long productId);

    @Modifying
    @Query("update ProductDetail p set p.storageQty = (p.storageQty + :soldQty) where p.id=:productVariantId")
    void updateQuantityIncrease(@Param("soldQty") Integer soldQty, @Param("productVariantId") Long productVariantId);

    @Modifying
    @Query("update ProductDetail p set p.storageQty = (p.storageQty - :soldQty), p.soldQty = (p.soldQty + :soldQty) where p.id=:productVariantId")
    void updateQuantityDecrease(@Param("soldQty") Integer soldQty, @Param("productVariantId") Long productVariantId);

    @Query("select sum(p.storageQty) from ProductDetail p where p.status = 'A'")
    Integer countTotalQuantity();

    @Query("from ProductDetail p where p.storageQty - p.defectiveQty = 0")
    Page<ProductDetail> findProductsOutOfStock(Pageable pageable);

    @Query("from ProductDetail p where p.expiryDate = :expiryDate")
    List<ProductDetail> findByExpiryDate(LocalDate expiryDate);

    @Query("from ProductDetail p where p.defectiveQty > 0")
    List<ProductDetail> findDefective();

    @Query("""
        SELECT new com.flowiee.pms.model.ProductSummaryInfoModel(
            pd.product.id,
            pd.color.id,
            pd.color.name,
            pd.size.id,
            pd.size.name,
            SUM(COALESCE(pd.storageQty, 0)))
        FROM com.flowiee.pms.entity.product.ProductDetail pd
        WHERE (coalesce(:productIds, -1) = -1 or pd.product.id in :productIds)
        GROUP BY pd.product.id, pd.color.id, pd.color.name, pd.size.id, pd.size.name
    """)
    List<ProductSummaryInfoModel> findProductVariantInfo(@Param("productIds") List<Long> productIds);

    @Query("select p.product from ProductDetail p where coalesce(:variantIds, -1) = -1 or p.product.id in :variantIds")
    List<Product> findProductByVariantIds(@Param("variantIds") List<Long> variantIds);
}