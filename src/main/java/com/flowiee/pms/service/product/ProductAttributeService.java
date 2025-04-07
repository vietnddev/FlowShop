package com.flowiee.pms.service.product;

import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.model.dto.ProductAttributeDTO;
import org.springframework.data.domain.Page;

public interface ProductAttributeService extends BaseCurdService<ProductAttributeDTO> {
    Page<ProductAttributeDTO> findAll(int pageSize, int pageNum, Long pProductDetailId);
}