package com.flowiee.pms.modules.product.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.product.entity.ProductDamaged;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDamagedRepository extends BaseRepository<ProductDamaged, Long> {
}