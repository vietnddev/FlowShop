package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.shared.base.BaseRepository;
import com.flowiee.pms.modules.inventory.entity.ProductRelated;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRelatedRepository extends BaseRepository<ProductRelated, Long> {
    List<ProductRelated> findByProductId(Long productId);
}