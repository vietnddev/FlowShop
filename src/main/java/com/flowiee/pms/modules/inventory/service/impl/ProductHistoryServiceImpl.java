package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.modules.inventory.entity.*;
import com.flowiee.pms.modules.inventory.dto.ProductHistoryDTO;
import com.flowiee.pms.modules.inventory.repository.ProductHistoryRepository;
import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.modules.inventory.repository.ProductPriceHistoryRepository;
import com.flowiee.pms.modules.inventory.service.ProductHistoryService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductHistoryServiceImpl extends BaseService<ProductHistory, ProductHistoryDTO, ProductHistoryRepository> implements ProductHistoryService {
    private final ProductPriceHistoryRepository mvProductPriceHistoryRepository;

    public ProductHistoryServiceImpl(ProductHistoryRepository pProductHistoryRepository, ProductPriceHistoryRepository pProductPriceHistoryRepository) {
        super(ProductHistory.class, ProductHistoryDTO.class, pProductHistoryRepository);
        this.mvProductPriceHistoryRepository= pProductPriceHistoryRepository;
    }

    @Override
    public List<ProductHistoryDTO>find(BaseParameter pParam) {
        return super.convertDTOs(mvEntityRepository.findAll());
    }

    @Override
    public ProductHistoryDTO findById(Long productHistoryId, boolean pThrowException) {
        return super.findDtoById(productHistoryId, pThrowException);
    }

    @Override
    public ProductHistoryDTO save(ProductHistoryDTO pDto) {
        return ProductHistoryDTO.toDto(mvEntityRepository.save(ProductHistory.builder()
                .product(new Product(pDto.getProduct().getId()))
                .productDetail(new ProductDetail(pDto.getProductDetail().getId()))
                .title(pDto.getTitle())
                .field(pDto.getField())
                .oldValue(pDto.getOldValue())
                .newValue(pDto.getNewValue())
                .build()));
    }

    @Override
    public ProductHistoryDTO update(ProductHistoryDTO pProductHistory, Long pProductHistoryId) {
        return super.update(pProductHistory, pProductHistoryId);
    }

    @Override
    public String delete(Long productHistoryId) {
        return super.delete(productHistoryId);
    }

    @Override
    public List<ProductHistory> findByProduct(Long productId) {
        List<ProductHistory> histories = new ArrayList<>();
        histories.addAll(mvEntityRepository.findByProductId(productId));
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

        ProductHistory history = new ProductHistory();
        history.setProductVariantId(price.getProductVariant().getId());
        history.setTitle(price.getPriceType().getDescription());
        history.setField(price.getPriceType().name());
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
            logSaved.add(mvEntityRepository.save(productHistory));
        }
        return logSaved;
    }
}