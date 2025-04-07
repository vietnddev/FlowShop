package com.flowiee.pms.service.product;

import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.model.dto.ProductReviewDTO;
import org.springframework.data.domain.Page;

public interface ProductReviewService extends BaseCurdService<ProductReviewDTO> {
    Page<ProductReviewDTO> findByProduct(Long pProductId);
}