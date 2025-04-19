package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.product.dto.MaterialHistoryDTO;

import java.util.List;
import java.util.Map;

public interface MaterialHistoryService extends BaseCurdService<MaterialHistoryDTO> {
    List<MaterialHistoryDTO> findByMaterialId(Long materialId);

    List<MaterialHistoryDTO> findByFieldName(String action);

    List<MaterialHistoryDTO> save(Map<String, Object[]> logChanges, String title, Long materialId);
}