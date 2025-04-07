package com.flowiee.pms.service.product;

import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.model.dto.MaterialDTO;
import org.springframework.data.domain.Page;

public interface  MaterialService extends BaseCurdService<MaterialDTO> {
    Page<MaterialDTO> findAll(int pageSize, int pageNum, Long supplierId, Long unitId, String code, String name, String location, String status);

    void updateQuantity(Integer quantity, long materialId, String type);
}