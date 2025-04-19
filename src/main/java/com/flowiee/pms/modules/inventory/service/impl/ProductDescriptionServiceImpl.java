package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.modules.inventory.entity.ProductDescription;
import com.flowiee.pms.modules.inventory.dto.ProductDescriptionDTO;
import com.flowiee.pms.modules.inventory.repository.ProductDescriptionRepository;
import com.flowiee.pms.modules.inventory.service.ProductDescriptionService;
import org.springframework.stereotype.Service;

@Service
public class ProductDescriptionServiceImpl extends BaseService<ProductDescription, ProductDescriptionDTO, ProductDescriptionRepository>
        implements ProductDescriptionService {
    public ProductDescriptionServiceImpl(ProductDescriptionRepository pProductDescriptionRepository) {
        super(ProductDescription.class, ProductDescriptionDTO.class, pProductDescriptionRepository);
    }

    @Override
    public ProductDescriptionDTO findByProductId(Long pProductId) {
        return convertDTO(mvEntityRepository.findByProductId(pProductId));
    }
}