package com.flowiee.pms.product.service;

import com.flowiee.pms.product.dto.ProductRelatedDTO;

import java.util.List;

public interface ProductRelatedService {
    List<ProductRelatedDTO> get(Long productId);

    void add(Long productId, Long productRelatedId);

    void remove(Long relationId);
}