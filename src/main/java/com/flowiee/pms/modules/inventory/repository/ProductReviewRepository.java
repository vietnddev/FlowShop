package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.shared.base.BaseRepository;
import com.flowiee.pms.modules.inventory.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReviewRepository extends BaseRepository<ProductReview, Long> {
    Page<ProductReview> findByProduct(Long productId, Pageable pageable);
}