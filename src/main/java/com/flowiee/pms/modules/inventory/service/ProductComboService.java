package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.modules.inventory.dto.ProductComboDTO;
import org.springframework.data.domain.Page;

public interface ProductComboService extends ICurdService<ProductComboDTO> {
    Page<ProductComboDTO> findAll(int pageSize, int pageNum);
}