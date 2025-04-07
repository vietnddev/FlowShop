package com.flowiee.pms.service.product;

import com.flowiee.pms.model.dto.ProductDescriptionDTO;

public interface ProductDescriptionService {
    ProductDescriptionDTO findByProductId(Long pProductId);
}