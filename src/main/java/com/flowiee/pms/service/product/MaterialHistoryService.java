package com.flowiee.pms.service.product;

import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.model.dto.MaterialHistoryDTO;

import java.util.List;
import java.util.Map;

public interface MaterialHistoryService extends BaseCurdService<MaterialHistoryDTO> {
    List<MaterialHistoryDTO> findByMaterialId(Long materialId);

    List<MaterialHistoryDTO> findByFieldName(String action);

    List<MaterialHistoryDTO> save(Map<String, Object[]> logChanges, String title, Long materialId);
}