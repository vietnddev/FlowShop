package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.product.dto.ProductComboDTO;
import org.springframework.data.domain.Page;

public interface ProductComboService extends BaseCurdService<ProductComboDTO> {
    Page<ProductComboDTO> findAll(int pageSize, int pageNum);
}