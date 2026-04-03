package com.flowiee.pms.product.repository;

import com.flowiee.pms.shared.base.BaseRepository;
import com.flowiee.pms.product.entity.ProductRelated;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRelatedRepository extends BaseRepository<ProductRelated, Long> {
    List<ProductRelated> findByProductId(Long productId);
}