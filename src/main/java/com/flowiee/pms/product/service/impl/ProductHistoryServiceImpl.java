package com.flowiee.pms.product.service.impl;

import com.flowiee.pms.product.entity.*;
import com.flowiee.pms.product.repository.ProductHistoryRepository;
import com.flowiee.pms.product.repository.ProductPriceHistoryRepository;
import com.flowiee.pms.product.service.ProductHistoryService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductHistoryServiceImpl implements ProductHistoryService {
    private final ProductPriceHistoryRepository mvProductPriceHistoryRepository;
    private final ProductHistoryRepository mvProductHistoryRepository;

    @Override
    public List<ProductHistory> findByProduct(Long productId) {
        List<ProductHistory> histories = new ArrayList<>();
        histories.addAll(mvProductHistoryRepository.findByProductId(productId));
        histories.addAll(mvProductPriceHistoryRepository.findByProductId(productId)
                        .stream()
                        .map(this::mapToProductHistory)
                        .toList());
        return histories.stream()
                .sorted(Comparator.comparing(ProductHistory::getCreatedAt).reversed())
                .toList();
    }

    private ProductHistory mapToProductHistory(ProductPriceHistory pph) {
        ProductPrice price = pph.getProductPrice();
        ProductDetail productVariant = price.getProductVariant();

        ProductHistory history = new ProductHistory();
        history.setProductVariantId(price.getProductVariant().getId());
        history.setTitle(String.format("%s (%s) was changed price", productVariant.getVariantName(), productVariant.getVariantCode()));
        history.setField(String.format("%s (%s)", price.getPriceType().name(), price.getPriceType().getDescription()));
        history.setOldValue(pph.getOldPrice().toPlainString());
        history.setNewValue(pph.getNewPrice().toPlainString());
        history.setCreatedBy(pph.getChangedBy() != null ? pph.getChangedBy().getId() : -1);
        history.setCreatedAt(pph.getChangeTime());

        return history;
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
            logSaved.add(mvProductHistoryRepository.save(productHistory));
        }
        return logSaved;
    }
}