package com.flowiee.pms.modules.media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.flowiee.pms.modules.media.entity.FileStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface FileStorageRepository extends JpaRepository<FileStorage, Long> {
    @Query("SELECT f FROM FileStorage f " +
            "WHERE (:module IS NULL OR f.module = :module) " +
            "AND (:productId IS NULL OR (f.product.id = :productId AND f.productDetail IS NULL)) " +
            "AND (:productVariantId IS NULL OR f.productDetail.id = :productVariantId) " +
            "ORDER BY f.createdAt")
    List<FileStorage> findAllImages(@Param("module") String module,
                                    @Param("productId") Long productId,
                                    @Param("productVariantId") Long productVariantId);

    @Query("SELECT f FROM FileStorage f " +
            "WHERE (:productId IS NULL OR (f.product.id = :productId AND f.productDetail IS NULL)) " +
            "AND (:productVariantId IS NULL OR f.productDetail.id = :productVariantId) " +
            "AND f.isActive = true " +
            "ORDER BY f.createdAt")
    Optional<FileStorage> findProductImageActive(@Param("productId") Long productId,
                                                 @Param("productVariantId") Long productVariantId);

//    @Query("from FileStorage f " +
//            "where 1=1 " +
//            "and (:productId is null or (f.product.id in :productId and f.productDetail.id is null)) " +
//            "and (:productVariantId is null or f.productDetail.id in :productVariantId) " +
//            "and f.isActive is true " +
//            "order by f.createdAt")
//    List<FileStorage> findActiveImage(@Param("productId") List<Long> productId, @Param("productVariantId") List<Long> productVariantId);

    @Query("from FileStorage f " +
            "where f.isActive = true " +
            "and f.product.id in (:ids) " +
            "order by f.createdAt")
    List<FileStorage> findProductImageActive(@Param("ids") List<Long> ids);

    @Query("from FileStorage f " +
           "where f.isActive = true " +
           "and f.productDetail.id in (:ids) " +
           "order by f.createdAt")
    List<FileStorage> findProductVariantImageActive(@Param("ids") List<Long> ids);

    @Query("from FileStorage f where f.order.id=:orderId")
    FileStorage findQRCodeOfOrder(@Param("orderId") Long orderId);

    @Query("from FileStorage f " +
           "where f.transactionGoods.id = :tranId " +
           "order by f.createdAt")
    List<FileStorage> findByTransactionGoodsId(@Param("tranId") Long pTransactionImportId);
}