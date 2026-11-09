package com.flowiee.pms.product.repository;

import com.flowiee.pms.product.entity.ProductStockMovement;
import com.flowiee.pms.shared.base.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductStockMovementRepository extends BaseRepository<ProductStockMovement, Long> {
}