package com.flowiee.pms.inventory.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.inventory.dto.MaterialDTO;
import org.springframework.data.domain.Page;

public interface  MaterialService extends ICurdService<MaterialDTO> {
    Page<MaterialDTO> find(int pageSize, int pageNum, Long supplierId, Long unitId, String code, String name, String location, String status);

    void updateQuantity(Integer quantity, long materialId, String type);
}