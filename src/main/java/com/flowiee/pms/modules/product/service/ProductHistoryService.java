package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.product.entity.ProductHistory;
import com.flowiee.pms.modules.product.dto.ProductHistoryDTO;

import java.util.List;
import java.util.Map;

public interface ProductHistoryService extends BaseCurdService<ProductHistoryDTO> {
    List<ProductHistory> findByProduct(Long productId);

    List<ProductHistory> findPriceChange(Long productDetailId);

    List<ProductHistory> save(Map<String, Object[]> logChanges, String title, Long productBaseId, Long productVariantId, Long productAttributeId);
}