package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.base.service.BaseGService;
import com.flowiee.pms.modules.product.entity.ProductDescription;
import com.flowiee.pms.modules.product.dto.ProductDescriptionDTO;
import com.flowiee.pms.modules.product.repository.ProductDescriptionRepository;
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