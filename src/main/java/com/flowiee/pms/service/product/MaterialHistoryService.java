package com.flowiee.pms.service.product;

import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.entity.product.MaterialHistory;

import java.util.List;
import java.util.Map;

public interface MaterialHistoryService extends BaseCurdService<MaterialHistory> {
    List<MaterialHistory> findByMaterialId(Long materialId);

    List<MaterialHistory> findByFieldName(String action);

    List<MaterialHistory> save(Map<String, Object[]> logChanges, String title, Long materialId);
}