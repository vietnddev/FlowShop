package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.inventory.entity.ProductHistory;
import com.flowiee.pms.modules.inventory.dto.ProductHistoryDTO;

import java.util.List;
import java.util.Map;

public interface ProductHistoryService extends ICurdService<ProductHistoryDTO> {
    List<ProductHistory> findByProduct(Long productId);

    List<ProductHistory> findPriceChange(Long productDetailId);

    List<ProductHistory> save(Map<String, Object[]> logChanges, String title, Long productBaseId, Long productVariantId, Long productAttributeId);
}