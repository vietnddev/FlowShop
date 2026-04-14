package com.flowiee.pms.product.service.impl;

import com.flowiee.pms.shared.base.BaseService;
import com.flowiee.pms.shared.util.CoreUtils;
import com.flowiee.pms.product.entity.ProductDetail;
import com.flowiee.pms.product.entity.ProductPrice;
import com.flowiee.pms.product.dto.ProductPriceDTO;
import com.flowiee.pms.product.entity.ProductPriceHistory;
import com.flowiee.pms.product.enums.PriceChangeType;
import com.flowiee.pms.product.enums.PriceType;
import com.flowiee.pms.product.repository.ProductPriceHistoryRepository;
import com.flowiee.pms.product.repository.ProductPriceRepository;
import com.flowiee.pms.product.service.ProductPriceService;
import com.flowiee.pms.shared.util.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductPriceServiceImpl extends BaseService<ProductPrice, ProductPriceDTO, ProductPriceRepository> implements ProductPriceService {
    private final ProductPriceHistoryRepository mvProductPriceHistoryRepository;

    public ProductPriceServiceImpl(ProductPriceRepository pEntityRepository, ProductPriceHistoryRepository pProductPriceHistoryRepository) {
        super(ProductPrice.class, ProductPriceDTO.class, pEntityRepository);
        this.mvProductPriceHistoryRepository = pProductPriceHistoryRepository;
    }

    @Override
    public ProductPriceDTO getPrices(Long productVariantId) {
        return mvEntityRepository.findPricesByVariantIds(List.of(productVariantId))
                .stream()
                .findFirst()
                .orElseGet(() -> ProductPriceDTO.builder()
                        .retailPrice(BigDecimal.ZERO)
                        .retailPriceDiscount(BigDecimal.ZERO)
                        .wholesalePrice(BigDecimal.ZERO)
                        .wholesalePriceDiscount(BigDecimal.ZERO)
                        .purchasePrice(BigDecimal.ZERO)
                        .costPrice(BigDecimal.ZERO)
                        .build()
                );
    }

    @Override
    public Map<Long, ProductPriceDTO> getPrices(List<Long> pProductVariantIds) {
        if (CollectionUtils.isEmpty(pProductVariantIds)) {
            return Map.of();
        }

        List<ProductPriceDTO> lvPriceList = new ArrayList<>();
        int batchSize = 1000; // Số lượng tối đa trong một truy vấn
        for (int i = 0; i < pProductVariantIds.size(); i += batchSize) {
            List<Long> batch = new ArrayList<>(pProductVariantIds.subList(i, Math.min(i + batchSize, pProductVariantIds.size())));
            lvPriceList.addAll(mvEntityRepository.findPricesByVariantIds(batch));
        }

        return lvPriceList.stream()
                .collect(Collectors.toMap(
                        ProductPriceDTO::getProductVariantId,
                        price -> price,
                        (existing, replacement) -> existing
                ));
    }

    @Override
    public ProductPriceDTO create(ProductPriceDTO pPriceDTO) {
        Long lvProductVariantId = pPriceDTO.getProductVariantId();
        BigDecimal lvRetailPrice = CoreUtils.coalesce(pPriceDTO.getRetailPrice());
        BigDecimal lvWholesalePrice = CoreUtils.coalesce(pPriceDTO.getWholesalePrice());
        BigDecimal lvCostPrice = CoreUtils.coalesce(pPriceDTO.getCostPrice());

        for (PriceType lvType : PriceType.values()) {
            ProductPrice lvPriceModel = ProductPrice.builder()
                    .state(ProductPrice.STATE_ACTIVE)
                    .productVariant(new ProductDetail(lvProductVariantId))
                    .priceType(lvType)
                    .build();
            if (lvType.equals(PriceType.RTL)) {
                lvPriceModel.setPriceValue(lvRetailPrice);
                if (!mvEntityRepository.existsByStateAndTypeAndProductVariantId(ProductPrice.STATE_ACTIVE, PriceType.RTL, lvProductVariantId)) {
                    mvEntityRepository.save(lvPriceModel);
                }
            }
            else if (lvType.equals(PriceType.WHO)) {
                lvPriceModel.setPriceValue(lvWholesalePrice);
                if (!mvEntityRepository.existsByStateAndTypeAndProductVariantId(ProductPrice.STATE_ACTIVE, PriceType.WHO, lvProductVariantId)) {
                    mvEntityRepository.save(lvPriceModel);
                }
            }
            else if (lvType.equals(PriceType.CSP)) {
                lvPriceModel.setPriceValue(lvCostPrice);
                if (!mvEntityRepository.existsByStateAndTypeAndProductVariantId(ProductPrice.STATE_ACTIVE, PriceType.CSP, lvProductVariantId)) {
                    mvEntityRepository.save(lvPriceModel);
                }
            }
        }

        return getPrices(lvProductVariantId);
    }

    @Transactional
    @Override
    public ProductPriceDTO updatePrice(Long pProductVariantId, ProductPriceDTO pRequestPrice) {
        List<ProductPrice> lvPrices = mvEntityRepository.findByVariantId(pProductVariantId);
        for (ProductPrice lvPrice : lvPrices) {
            if (PriceType.RTL.equals(lvPrice.getPriceType())) {
                handlePriceUpdate(lvPrice, pRequestPrice.getRetailPrice());
            }
            if (PriceType.WHO.equals(lvPrice.getPriceType())) {
                handlePriceUpdate(lvPrice, pRequestPrice.getWholesalePrice());
            }
            if (PriceType.CSP.equals(lvPrice.getPriceType())) {
                handlePriceUpdate(lvPrice, pRequestPrice.getCostPrice());
            }
        }

        return getPrices(pProductVariantId);
    }

    private void handlePriceUpdate(ProductPrice pCurrentPriceEntity, BigDecimal newPrice) {
        if (newPrice == null) {
            return;
        }

        if (pCurrentPriceEntity.getPriceValue().compareTo(newPrice) != 0) {
            mvEntityRepository.inactivePrice(pCurrentPriceEntity.getId());
            createNewPrice(pCurrentPriceEntity.getProductVariant(), pCurrentPriceEntity.getPriceType(), newPrice);
            createPriceHistory(pCurrentPriceEntity, newPrice);
        }
    }

    private void createNewPrice(ProductDetail productVariant, PriceType priceType, BigDecimal priceValue) {
        mvEntityRepository.save(ProductPrice.builder()
                .state(ProductPrice.STATE_ACTIVE)
                .productVariant(productVariant)
                .priceType(priceType)
                .priceValue(priceValue)
                .build());
    }

    private void createPriceHistory(ProductPrice pCurrentPriceEntity, BigDecimal newPrice) {
        mvProductPriceHistoryRepository.save(ProductPriceHistory.builder()
                .productPrice(pCurrentPriceEntity)
                .changeType(PriceChangeType.MANUAL)
                .oldPrice(pCurrentPriceEntity.getPriceValue())
                .newPrice(newPrice)
                .changeTime(LocalDateTime.now())
                .changedBy(SecurityUtils.getCurrentUser().getEntity())
                .reason("-")
                .build());
    }
}