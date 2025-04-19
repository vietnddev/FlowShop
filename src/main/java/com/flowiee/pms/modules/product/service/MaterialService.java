package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.product.dto.MaterialDTO;
import org.springframework.data.domain.Page;

public interface  MaterialService extends BaseCurdService<MaterialDTO> {
    Page<MaterialDTO> findAll(int pageSize, int pageNum, Long supplierId, Long unitId, String code, String name, String location, String status);

    void updateQuantity(Integer quantity, long materialId, String type);
}