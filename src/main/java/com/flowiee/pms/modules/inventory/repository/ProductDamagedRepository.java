package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.inventory.entity.ProductDamaged;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDamagedRepository extends BaseRepository<ProductDamaged, Long> {
}