package com.flowiee.pms.service.product;

import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.entity.product.ProductHistory;
import com.flowiee.pms.model.dto.ProductHistoryDTO;

import java.util.List;
import java.util.Map;

public interface ProductHistoryService extends BaseCurdService<ProductHistoryDTO> {
    List<ProductHistory> findByProduct(Long productId);

    List<ProductHistory> findPriceChange(Long productDetailId);

    List<ProductHistory> save(Map<String, Object[]> logChanges, String title, Long productBaseId, Long productVariantId, Long productAttributeId);
}