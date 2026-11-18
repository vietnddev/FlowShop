package com.flowiee.pms.product.repository;

import com.flowiee.pms.shared.base.BaseRepository;
import com.flowiee.pms.shared.base.SoftDeleteRepository;
import com.flowiee.pms.product.entity.ProductDetail;
import com.flowiee.pms.product.model.ProductSummaryInfoModel;
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

    @Query("""
        select coalesce(sum(i.onHandQuantity), 0)
        from ProductInventory i
        join i.variant p
        where p.status = 'A'
            and p.deletedAt is null
    """)
    Integer getTotalOnHandQuantity();

    @Query("select coalesce(sum(od.quantity), 0) " +
           "from OrderDetail od " +
           "where od.productDetail.id = :productVariantId" +
           "    and od.order.orderStatus = 'COMPLETED'")
    Integer getTotalSoldQuantity(@Param("productVariantId") Long productVariantId);

    @Query("""
        from ProductDetail p
        join p.productInventory i
        where (i.onHandQuantity = 0
            or (i.reorderLevel is not null and i.onHandQuantity < i.reorderLevel))
            and p.deletedAt is null
    """)
    List<ProductDetail> findProductsOutOfStock();

    @Query("from ProductDetail p where p.expiryDate = :expiryDate and p.deletedAt is null")
    List<ProductDetail> findByExpiryDate(LocalDate expiryDate);

    @Query("from ProductDetail p where p.productInventory.defectiveQuantity > 0 and p.deletedAt is null")
    List<ProductDetail> findDefective();

    @Query("""
        SELECT new com.flowiee.pms.product.model.ProductSummaryInfoModel(
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
            SUM(COALESCE(pd.productInventory.onHandQuantity, 0)),
            SUM(COALESCE(pd.productInventory.onHandQuantity, 0)))
        FROM com.flowiee.pms.product.entity.ProductDetail pd
        WHERE (coalesce(:productIds, -1) = -1 or pd.product.id in :productIds) and pd.deletedAt is null
        GROUP BY pd.id, pd.variantCode, pd.variantName, pd.product.id, pd.fabricType.id, pd.fabricType.name, pd.color.id, pd.color.name, pd.size.id, pd.size.name, pd.status
    """)
    List<ProductSummaryInfoModel> findProductVariantInfo(@Param("productIds") List<Long> productIds);
}