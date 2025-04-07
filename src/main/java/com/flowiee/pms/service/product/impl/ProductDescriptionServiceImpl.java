package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.base.service.BaseServiceNew;
import com.flowiee.pms.entity.product.ProductDescription;
import com.flowiee.pms.model.dto.ProductDescriptionDTO;
import com.flowiee.pms.repository.product.ProductDescriptionRepository;
import com.flowiee.pms.service.product.ProductDescriptionService;
import org.springframework.stereotype.Service;

@Service
public class ProductDescriptionServiceImpl extends BaseServiceNew<ProductDescription, ProductDescriptionDTO, ProductDescriptionRepository> implements ProductDescriptionService {
    public ProductDescriptionServiceImpl(ProductDescriptionRepository pProductDescriptionRepository) {
        super(ProductDescription.class, ProductDescriptionDTO.class, pProductDescriptionRepository);
    }

    @Override
    public ProductDescription findByProductId(Long pProductId) {
        return mvEntityRepository.findByProductId(pProductId);
    }
}