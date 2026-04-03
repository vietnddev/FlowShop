package com.flowiee.pms.product.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.product.entity.ProductHistory;
import com.flowiee.pms.product.dto.ProductHistoryDTO;

import java.util.List;
import java.util.Map;

public interface ProductHistoryService extends ICurdService<ProductHistoryDTO> {
    List<ProductHistory> findByProduct(Long productId);

    List<ProductHistory> save(Map<String, Object[]> logChanges, String title, Long productBaseId, Long productVariantId, Long productAttributeId);
}