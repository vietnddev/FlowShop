package com.flowiee.pms.product.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.product.dto.ProductComboDTO;
import org.springframework.data.domain.Page;

public interface ProductComboService extends ICurdService<ProductComboDTO> {
    Page<ProductComboDTO> findAll(int pageSize, int pageNum);
}