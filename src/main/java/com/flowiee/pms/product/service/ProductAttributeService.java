package com.flowiee.pms.product.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.product.dto.ProductAttributeDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductAttributeService extends ICurdService<ProductAttributeDTO> {
    Page<ProductAttributeDTO> findAll(int pageSize, int pageNum, Long pProductDetailId);

    List<ProductAttributeDTO> saveAll(Long pProductId, List<ProductAttributeDTO> pAttributes);
}