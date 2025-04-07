package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.base.service.BaseGService;
import com.flowiee.pms.entity.product.Product;
import com.flowiee.pms.entity.product.ProductRelated;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.model.dto.ProductRelatedDTO;
import com.flowiee.pms.repository.product.ProductRelatedRepository;
import com.flowiee.pms.repository.product.ProductRepository;
import com.flowiee.pms.service.product.ProductRelatedService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductRelatedServiceImpl extends BaseGService<ProductRelated, ProductRelatedDTO, ProductRelatedRepository> implements ProductRelatedService {
    private final ProductRepository mvProductRepository;

    public ProductRelatedServiceImpl(ProductRelatedRepository pEntityRepository, ProductRepository pProductRepository) {
        super(ProductRelated.class, ProductRelatedDTO.class, pEntityRepository);
        this.mvProductRepository = pProductRepository;
    }

    @Override
    public List<ProductRelatedDTO> get(Long productId) {
        Product product = mvProductRepository.findById(productId)
                .orElseThrow(()-> new EntityNotFoundException(new Object[] {"product"}, null, null));
        return convertDTOs(mvEntityRepository.findByProductId(product.getId()));
    }

    @Override
    public void add(Long productId, Long productRelatedId) {
        Product product = mvProductRepository.findById(productId)
                .orElseThrow(()-> new EntityNotFoundException(new Object[] {"product"}, null, null));

        Product relatedProduct = mvProductRepository.findById(productRelatedId)
                .orElseThrow(()-> new EntityNotFoundException(new Object[] {"related product"}, null, null));

        ProductRelated relation = new ProductRelated();
        relation.setProduct(product);
        relation.setRelatedProduct(relatedProduct);

        mvEntityRepository.save(relation);
    }

    @Override
    public void remove(Long pRelationId) {
        super.delete(pRelationId);
    }
}