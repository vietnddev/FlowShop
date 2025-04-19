package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.modules.inventory.dto.ProductDescriptionDTO;

public interface ProductDescriptionService {
    ProductDescriptionDTO findByProductId(Long pProductId);
}