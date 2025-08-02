package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.inventory.dto.ProductAttributeDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductAttributeService extends ICurdService<ProductAttributeDTO> {
    Page<ProductAttributeDTO> findAll(int pageSize, int pageNum, Long pProductDetailId);

    List<ProductAttributeDTO> saveAll(List<ProductAttributeDTO> pAttributes);
}