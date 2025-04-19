package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.product.dto.ProductReviewDTO;
import org.springframework.data.domain.Page;

public interface ProductReviewService extends BaseCurdService<ProductReviewDTO> {
    Page<ProductReviewDTO> findByProduct(Long pProductId);
}