package com.flowiee.pms.repository.product;

import com.flowiee.pms.base.BaseRepository;
import com.flowiee.pms.entity.product.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReviewRepository extends BaseRepository<ProductReview, Long> {
    Page<ProductReview> findByProduct(Long productId, Pageable pageable);
}