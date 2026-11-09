package com.flowiee.pms.product.repository;

import com.flowiee.pms.product.entity.ProductInventory;
import com.flowiee.pms.shared.base.BaseRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface ProductInventoryRepository extends BaseRepository<ProductInventory, Long> {
    Optional<ProductInventory> findByVariantId(Long variantId);

    boolean existsByVariantId(Long variantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT i
        FROM ProductInventory i
        WHERE i.variant.id = :variantId
    """)
    Optional<ProductInventory> findByVariantIdForUpdate(@Param("variantId") Long variantId);
}