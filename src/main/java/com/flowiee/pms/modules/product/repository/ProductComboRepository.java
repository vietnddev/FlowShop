package com.flowiee.pms.modules.product.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.product.entity.ProductCombo;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductComboRepository extends BaseRepository<ProductCombo, Long> {
}