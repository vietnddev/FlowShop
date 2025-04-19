package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.inventory.dto.ProductReviewDTO;
import org.springframework.data.domain.Page;

public interface ProductReviewService extends ICurdService<ProductReviewDTO> {
    Page<ProductReviewDTO> findByProduct(Long pProductId);
}