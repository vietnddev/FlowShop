package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.inventory.dto.MaterialHistoryDTO;

import java.util.List;
import java.util.Map;

public interface MaterialHistoryService extends ICurdService<MaterialHistoryDTO> {
    List<MaterialHistoryDTO> findByMaterialId(Long materialId);

    List<MaterialHistoryDTO> findByFieldName(String action);

    List<MaterialHistoryDTO> save(Map<String, Object[]> logChanges, String title, Long materialId);
}