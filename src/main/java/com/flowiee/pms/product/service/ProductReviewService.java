package com.flowiee.pms.product.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.product.dto.ProductReviewDTO;
import org.springframework.data.domain.Page;

public interface ProductReviewService extends ICurdService<ProductReviewDTO> {
    Page<ProductReviewDTO> findByProduct(Long pProductId);
}