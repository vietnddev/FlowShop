package com.flowiee.pms.service.product;

import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.entity.product.ProductHistory;

import java.util.List;
import java.util.Map;

public interface ProductHistoryService extends BaseCurdService<ProductHistory> {
    List<ProductHistory> findByProduct(Long productId);

    List<ProductHistory> findPriceChange(Long productDetailId);

    List<ProductHistory> save(Map<String, Object[]> logChanges, String title, Long productBaseId, Long productVariantId, Long productAttributeId);
}