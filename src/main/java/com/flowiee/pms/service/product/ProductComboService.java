package com.flowiee.pms.service.product;

import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.model.dto.ProductComboDTO;
import org.springframework.data.domain.Page;

public interface ProductComboService extends BaseCurdService<ProductComboDTO> {
    Page<ProductComboDTO> findAll(int pageSize, int pageNum);
}