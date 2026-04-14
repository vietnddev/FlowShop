package com.flowiee.pms.product.service;

import com.flowiee.pms.product.entity.ProductHistory;

import java.util.List;
import java.util.Map;

public interface ProductHistoryService {
    List<ProductHistory> findByProduct(Long productId);

    List<ProductHistory> save(Map<String, Object[]> logChanges, String title, Long productBaseId, Long productVariantId, Long productAttributeId);
}