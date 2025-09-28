package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.modules.inventory.entity.ProductVariantExim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductDetailTempRepository extends JpaRepository <ProductVariantExim, Long>{
    @Transactional
    @Modifying
    @Query("update ProductVariantExim p set p.quantity = (p.quantity + :quantity) where p.id =:productVariantTempId")
    void updateQuantityIncrease(@Param("productVariantTempId") Long productVariantTempId, @Param("quantity") int quantity);

    @Transactional
    @Modifying
    @Query("update ProductVariantExim p set p.storageQty=:storageQty where p.id =:productVariantTempId")
    void updateStorageQuantity(@Param("productVariantTempId") Long productVariantTempId, @Param("storageQty") int storageQty);

    @Query("from ProductVariantExim p where p.productVariant.product.id=:productId order by p.createdAt desc")
    List<ProductVariantExim> findByProductId(@Param("productId") Long productId);

    @Query("from ProductVariantExim p where p.productVariant.id=:productVariantId order by p.createdAt desc")
    List<ProductVariantExim> findByProductVariantId(@Param("productVariantId") Long productVariantId);
}