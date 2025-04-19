package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.modules.product.entity.ProductReview;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.product.dto.ProductReviewDTO;
import com.flowiee.pms.modules.product.repository.ProductReviewRepository;
import com.flowiee.pms.common.base.service.BaseGService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductReviewImplService extends BaseGService<ProductReview, ProductReviewDTO, ProductReviewRepository> implements ProductReviewService {
    public ProductReviewImplService(ProductReviewRepository pEntityRepository) {
        super(ProductReview.class, ProductReviewDTO.class, pEntityRepository);
    }

    @Override
    public List<ProductReviewDTO> findAll() {
        return convertDTOs(mvEntityRepository.findAll());
    }

    @Override
    public ProductReviewDTO findById(Long productReviewId, boolean pThrowException) {
        return super.findById(productReviewId, pThrowException);
    }

    @Override
    public ProductReviewDTO save(ProductReviewDTO productReview) {
        return super.save(productReview);
    }

    @Override
    public ProductReviewDTO update(ProductReviewDTO productReview, Long productReviewId) {
        ProductReview existingReview = super.findById(productReviewId).orElseThrow(() -> new BadRequestException());
        existingReview.setReviewContent(productReview.getReviewContent());
        existingReview.setRating(productReview.getRating());
        return convertDTO(mvEntityRepository.save(existingReview));
    }

    @Override
    public String delete(Long productReviewId) {
        return super.delete(productReviewId);
    }

    @Override
    public Page<ProductReviewDTO> findByProduct(Long pProductId) {
        Page<ProductReview> productReviewPage = mvEntityRepository.findByProduct(pProductId, Pageable.unpaged());
        return new PageImpl<>(convertDTOs(productReviewPage.getContent()), productReviewPage.getPageable(),
                productReviewPage.getTotalElements());
    }
}