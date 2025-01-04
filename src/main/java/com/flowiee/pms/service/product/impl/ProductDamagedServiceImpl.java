package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.entity.product.ProductDamaged;
import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.entity.system.FileStorage;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.repository.product.ProductDamagedRepository;
import com.flowiee.pms.repository.product.ProductDetailRepository;
import com.flowiee.pms.repository.product.ProductRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.product.ProductDamagedService;
import com.flowiee.pms.service.product.ProductImageService;
import com.flowiee.pms.common.enumeration.MessageCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductDamagedServiceImpl extends BaseService implements ProductDamagedService {
    private final ProductDamagedRepository productDamagedRepository;
    private final ProductDetailRepository  productDetailRepository;
    private final ProductRepository        productRepository;
    private final ProductImageService      productImageService;

    @Override
    public List<ProductDamaged> findAll() {
        return productDamagedRepository.findAll();
    }

    @Override
    public ProductDamaged findById(Long entityId, boolean pThrowException) {
        Optional<ProductDamaged> entityOptional = productDamagedRepository.findById(entityId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"product damaged"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public ProductDamaged save(ProductDamaged pEntity) {
        ProductDamaged productDamaged = pEntity;
        if (pEntity.getProductVariant() == null || pEntity.getProductVariant().getId() == null) {
            throw new BadRequestException("Product variant invalid!");
        }
        Optional<ProductDetail> lvProductVariantOpt = productDetailRepository.findById(pEntity.getProductVariant().getId());
        if (lvProductVariantOpt.isEmpty()) {
            throw new BadRequestException("Product variant invalid!");
        }
        ProductDetail lvProductVariant = lvProductVariantOpt.get();
        if (pEntity.getQuantity() > lvProductVariantOpt.get().getStorageQty()) {
            throw new BadRequestException("Số lượng sản phẩm hư hỏng không được nhiều hơn tồn kho!");
        }

        productDamaged.setProductVariant(lvProductVariant);
        productDamaged.setRecordedDate(LocalDateTime.now());
        ProductDamaged productDamagedSaved = productDamagedRepository.save(pEntity);

        if (pEntity.getImageList() != null) {
            for (FileStorage fv : pEntity.getImageList()) {
                try {
                    productImageService.saveImageProductDamaged(fv.getFileAttach(), productDamagedSaved.getId());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        lvProductVariant.setDefectiveQty(lvProductVariant.getDefectiveQty() + pEntity.getQuantity());
        productDetailRepository.save(lvProductVariant);

        return productDamagedSaved;
    }

    @Override
    public ProductDamaged update(ProductDamaged entity, Long entityId) {
        ProductDamaged productDamaged = this.findById(entityId, true);
        //
        return productDamagedRepository.save(productDamaged);
    }

    @Override
    public String delete(Long entityId) {
        productDamagedRepository.deleteById(entityId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }
}