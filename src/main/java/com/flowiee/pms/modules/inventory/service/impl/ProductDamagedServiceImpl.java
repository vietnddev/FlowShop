package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.modules.inventory.entity.ProductDamaged;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.inventory.dto.ProductDamagedDTO;
import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import com.flowiee.pms.modules.inventory.repository.ProductDamagedRepository;
import com.flowiee.pms.modules.inventory.repository.ProductDetailRepository;
import com.flowiee.pms.modules.inventory.repository.ProductRepository;
import com.flowiee.pms.modules.inventory.service.ProductDamagedService;
import com.flowiee.pms.modules.inventory.service.ProductImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductDamagedServiceImpl extends BaseService<ProductDamaged, ProductDamagedDTO, ProductDamagedRepository> implements ProductDamagedService {
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private final ProductDetailRepository mvProductDetailRepository;
    private final ProductImageService mvProductImageService;
    private final ProductRepository mvProductRepository;

    public ProductDamagedServiceImpl(ProductDamagedRepository pEntityRepository, ProductDetailRepository pProductDetailRepository,
                                     ProductRepository pProductRepository, ProductImageService pProductImageService) {
        super(ProductDamaged.class, ProductDamagedDTO.class, pEntityRepository);
        this.mvProductDetailRepository = pProductDetailRepository;
        this.mvProductRepository = pProductRepository;
        this.mvProductImageService = pProductImageService;
    }

    @Override
    public List<ProductDamagedDTO>find() {
        return super.find(new BaseParameter());
    }

    @Override
    public ProductDamagedDTO findById(Long pEntityId, boolean pThrowException) {
        return super.findDtoById(pEntityId, pThrowException);
    }

    @Override
    public ProductDamagedDTO save(ProductDamagedDTO pDto) {
        if (pDto.getProductVariant() == null || pDto.getProductVariant().getId() == null) {
            throw new BadRequestException("Product variant invalid!");
        }
        Optional<ProductDetail> lvProductVariantOpt = mvProductDetailRepository.findById(pDto.getProductVariant().getId());
        if (lvProductVariantOpt.isEmpty()) {
            throw new BadRequestException("Product variant invalid!");
        }
        ProductDetail lvProductVariant = lvProductVariantOpt.get();
        if (pDto.getQuantity() > lvProductVariantOpt.get().getStorageQty()) {
            throw new BadRequestException("Số lượng sản phẩm hư hỏng không được nhiều hơn tồn kho!");
        }

        pDto.setProductVariant(new ProductVariantDTO(lvProductVariant.getId()));
        pDto.setRecordedDate(LocalDateTime.now());
        ProductDamagedDTO productDamagedSaved = super.save(pDto);

        if (pDto.getImageList() != null) {
            for (FileStorage fv : pDto.getImageList()) {
                try {
                    mvProductImageService.saveImageProductDamaged(fv.getFileAttach(), productDamagedSaved.getId());
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }

        lvProductVariant.setDefectiveQty(lvProductVariant.getDefectiveQty() + pDto.getQuantity());
        mvProductDetailRepository.save(lvProductVariant);

        return productDamagedSaved;
    }

    @Override
    public ProductDamagedDTO update(ProductDamagedDTO entity, Long entityId) {
        ProductDamaged productDamaged = super.findById(entityId).orElseThrow(() -> new BadRequestException());
        //
        return super.convertDTO(mvEntityRepository.save(productDamaged));
    }

    @Override
    public String delete(Long pEntityId) {
        return super.delete(pEntityId);
    }
}