package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.modules.product.dto.ProductDescriptionDTO;

public interface ProductDescriptionService {
    ProductDescriptionDTO findByProductId(Long pProductId);
}