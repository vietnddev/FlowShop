package com.flowiee.pms.product.repository;

import com.flowiee.pms.shared.base.BaseRepository;
import com.flowiee.pms.product.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReviewRepository extends BaseRepository<ProductReview, Long> {
    Page<ProductReview> findByProduct(Long productId, Pageable pageable);
}