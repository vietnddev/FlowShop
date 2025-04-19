package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.base.service.BaseGService;
import com.flowiee.pms.modules.product.entity.Product;
import com.flowiee.pms.modules.product.entity.ProductRelated;
import com.flowiee.pms.common.exception.EntityNotFoundException;
import com.flowiee.pms.modules.product.dto.ProductRelatedDTO;
import com.flowiee.pms.modules.product.repository.ProductRelatedRepository;
import com.flowiee.pms.modules.product.repository.ProductRepository;
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