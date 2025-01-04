package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.entity.product.Product;
import com.flowiee.pms.entity.product.ProductAttribute;
import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.entity.product.ProductHistory;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.repository.product.ProductHistoryRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.product.ProductHistoryService;
import com.flowiee.pms.common.enumeration.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductHistoryServiceImpl extends BaseService implements ProductHistoryService {
    ProductHistoryRepository mvProductHistoryRepository;

    @Override
    public List<ProductHistory> findAll() {
        return mvProductHistoryRepository.findAll();
    }

    @Override
    public ProductHistory findById(Long productHistoryId, boolean pThrowException) {
        Optional<ProductHistory> entityOptional = mvProductHistoryRepository.findById(productHistoryId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"product history"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public ProductHistory save(ProductHistory productHistory) {
        return mvProductHistoryRepository.save(productHistory);
    }

    @Override
    public ProductHistory update(ProductHistory productHistory, Long productHistoryId) {
        productHistory.setId(productHistoryId);
        return mvProductHistoryRepository.save(productHistory);
    }

    @Override
    public String delete(Long productHistoryId) {
        mvProductHistoryRepository.deleteById(productHistoryId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public List<ProductHistory> findByProduct(Long productId) {
        return mvProductHistoryRepository.findByProductId(productId);
    }

    @Override
    public List<ProductHistory> findPriceChange(Long productDetailId) {
        List<ProductHistory> prices =  mvProductHistoryRepository.findHistoryChangeOfProductDetail(productDetailId, "PRICE");
        for (ProductHistory priceChange : prices) {
            if (priceChange.getProduct() != null) {
                priceChange.setProductId(priceChange.getProduct().getId());
                if (priceChange.getProductDetail() != null) {
                    priceChange.setProductVariantId(priceChange.getProductDetail().getId());
                }
            }
        }
        return prices;
    }

    @Override
    public List<ProductHistory> save(Map<String, Object[]> logChanges, String title, Long productBaseId, Long productVariantId, Long productAttributeId) {
        List<ProductHistory> logSaved = new ArrayList<>();
        for (Map.Entry<String, Object[]> entry : logChanges.entrySet()) {
            String field = entry.getKey();
            String oldValue = entry.getValue()[0] != null ? entry.getValue()[0].toString() : "-";
            String newValue = entry.getValue()[1] != null ? entry.getValue()[1].toString() : "-";
            ProductHistory productHistory = ProductHistory.builder()
                    .title(title)
                    .product(productBaseId != null ? new Product(productBaseId) : null)
                    .productDetail(productVariantId != null ? new ProductDetail(productVariantId) : null)
                    .productAttribute(productAttributeId != null ? new ProductAttribute(productAttributeId) : null)
                    .field(field)
                    .oldValue("null".equals(oldValue) || ObjectUtils.isEmpty(oldValue) ? "-" : oldValue)
                    .newValue("null".equals(newValue) || ObjectUtils.isEmpty(newValue) ? "-" : newValue)
                    .build();
            logSaved.add(this.save(productHistory));
        }
        return logSaved;
    }
}