package com.flowiee.pms.service.product;

import com.flowiee.pms.entity.product.ProductReview;
import com.flowiee.pms.base.BaseCurdService;
import org.springframework.data.domain.Page;

public interface ProductReviewService extends BaseCurdService<ProductReview> {
    Page<ProductReview> findByProduct(Long pProductId);
}