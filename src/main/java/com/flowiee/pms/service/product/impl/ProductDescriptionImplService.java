package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.base.service.BaseGService;
import com.flowiee.pms.entity.product.ProductDescription;
import com.flowiee.pms.model.dto.ProductDescriptionDTO;
import com.flowiee.pms.repository.product.ProductDescriptionRepository;
import com.flowiee.pms.service.product.ProductDescriptionService;
import org.springframework.stereotype.Service;

@Service
public class ProductDescriptionImplService extends BaseGService<ProductDescription, ProductDescriptionDTO, ProductDescriptionRepository>
        implements ProductDescriptionService {
    public ProductDescriptionImplService(ProductDescriptionRepository pProductDescriptionRepository) {
        super(ProductDescription.class, ProductDescriptionDTO.class, pProductDescriptionRepository);
    }

    @Override
    public ProductDescriptionDTO findByProductId(Long pProductId) {
        return convertDTO(mvEntityRepository.findByProductId(pProductId));
    }
}