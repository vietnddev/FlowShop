package com.flowiee.pms.repository.product;

import com.flowiee.pms.entity.product.ProductDetail;
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

    @Query("select sum(nvl(p.storageQty, 0)) from ProductDetail p where p.product.id=:productId and p.color.id=:colorId and p.size.id=:sizeId")
    Integer findQuantityBySizeOfEachColor(@Param("productId") Long productId, @Param("colorId") Long colorId, @Param("sizeId") Long sizeId);

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
}