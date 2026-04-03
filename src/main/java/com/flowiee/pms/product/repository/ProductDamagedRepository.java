package com.flowiee.pms.product.repository;

import com.flowiee.pms.shared.base.BaseRepository;
import com.flowiee.pms.product.entity.ProductDamaged;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDamagedRepository extends BaseRepository<ProductDamaged, Long> {
}