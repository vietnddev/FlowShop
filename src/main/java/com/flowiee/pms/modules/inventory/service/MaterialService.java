package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.inventory.dto.MaterialDTO;
import org.springframework.data.domain.Page;

public interface  MaterialService extends ICurdService<MaterialDTO> {
    Page<MaterialDTO> findAll(int pageSize, int pageNum, Long supplierId, Long unitId, String code, String name, String location, String status);

    void updateQuantity(Integer quantity, long materialId, String type);
}