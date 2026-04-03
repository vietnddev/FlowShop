package com.flowiee.pms.product.repository;

import com.flowiee.pms.shared.base.BaseRepository;
import com.flowiee.pms.shared.base.SoftDeleteRepository;
import com.flowiee.pms.product.entity.Product;
import com.flowiee.pms.product.entity.ProductDetail;
import com.flowiee.pms.product.model.ProductSummaryInfoModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductDetailRepository extends BaseRepository<ProductDetail, Long>, SoftDeleteRepository<ProductDetail, Long> {
    @Query("from ProductDetail b " +
           "where b.product.id=:productId and b.color.id=:colorId and b.size.id=:sizeId and b.fabricType.id=:fabricTypeId " +
           "    and b.deletedAt is null")
    ProductDetail findByColorAndSize(@Param("productId") Long productId, @Param("colorId") Long colorId, @Param("sizeId")  Long sizeId, @Param("fabricTypeId")  Long fabricTypeId);

    @Query("select sum(coalesce(p.soldQty, 0)) as totalQtySell from ProductDetail p " +
           "where p.product.id=:productId and p.deletedAt is null")
    Integer findTotalQtySell(@Param("productId") Long productId);

    @Modifying
    @Query("update ProductDetail p set p.storageQty = (p.storageQty + :quantity) where p.id=:productVariantId")
    void updateQuantityIncrease(@Param("quantity") Integer quantity, @Param("productVariantId") Long productVariantId);

    @Modifying
    @Query("update ProductDetail p set p.storageQty = (p.storageQty - :soldQty), p.soldQty = (p.soldQty + :soldQty) where p.id=:productVariantId")
    void updateQuantityDecrease(@Param("soldQty") Integer soldQty, @Param("productVariantId") Long productVariantId);

    @Query("select sum(p.storageQty) from ProductDetail p where p.status = 'A' and p.deletedAt is null")
    Integer countTotalQuantity();

    @Query("from ProductDetail p " +
           "where p.storageQty = 0 or (p.lowStockThreshold is not null and p.storageQty < p.lowStockThreshold) " +
           "    and p.deletedAt is null")
    List<ProductDetail> findProductsOutOfStock();

    @Query("from ProductDetail p where p.expiryDate = :expiryDate and p.deletedAt is null")
    List<ProductDetail> findByExpiryDate(LocalDate expiryDate);

    @Query("from ProductDetail p where p.defectiveQty > 0 and p.deletedAt is null")
    List<ProductDetail> findDefective();

    @Query("""
        SELECT new com.flowiee.pms.modules.inventory.model.ProductSummaryInfoModel(
            pd.id,
            pd.variantCode,
            pd.variantName,
            pd.product.id,
            pd.fabricType.id,
            pd.fabricType.name,
            pd.color.id,
            pd.color.name,
            pd.size.id,
            pd.size.name,
            pd.status,
            SUM(COALESCE(pd.storageQty, 0)),
            SUM(COALESCE(pd.soldQty, 0)))
        FROM com.flowiee.pms.modules.inventory.entity.ProductDetail pd
        WHERE (coalesce(:productIds, -1) = -1 or pd.product.id in :productIds) and pd.deletedAt is null
        GROUP BY pd.id, pd.variantCode, pd.variantName, pd.product.id, pd.fabricType.id, pd.fabricType.name, pd.color.id, pd.color.name, pd.size.id, pd.size.name, pd.status
    """)
    List<ProductSummaryInfoModel> findProductVariantInfo(@Param("productIds") List<Long> productIds);

    @Query("select p.product from ProductDetail p " +
           "where (coalesce(:variantIds, -1) = -1 or p.product.id in :variantIds) and p.deletedAt is null ")
    List<Product> findProductByVariantIds(@Param("variantIds") List<Long> variantIds);
}