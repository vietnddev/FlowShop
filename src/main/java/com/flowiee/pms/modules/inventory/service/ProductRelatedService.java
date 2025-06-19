package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.modules.inventory.dto.ProductRelatedDTO;

import java.util.List;

public interface ProductRelatedService {
    List<ProductRelatedDTO> get(Long productId);

    void add(Long productId, Long productRelatedId);

    void remove(Long relationId);
}