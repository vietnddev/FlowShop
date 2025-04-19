package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.product.dto.ProductAttributeDTO;
import org.springframework.data.domain.Page;

public interface ProductAttributeService extends BaseCurdService<ProductAttributeDTO> {
    Page<ProductAttributeDTO> findAll(int pageSize, int pageNum, Long pProductDetailId);
}