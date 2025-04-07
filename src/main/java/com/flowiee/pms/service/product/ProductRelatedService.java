package com.flowiee.pms.service.product;

import com.flowiee.pms.model.dto.ProductRelatedDTO;

import java.util.List;

public interface ProductRelatedService {
    List<ProductRelatedDTO> get(Long productId);

    void add(Long productId, Long productRelatedId);

    void remove(Long relationId);
}