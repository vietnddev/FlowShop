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
import com.flowiee.pms.product.service.ProductVariantService;
import com.flowiee.pms.shared.util.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductPriceServiceImpl extends BaseService<ProductPrice, ProductPriceDTO, ProductPriceRepository> implements ProductPriceService {
    private final ProductVariantService mvProductVariantService;
    private final ProductPriceHistoryRepository mvProductPriceHistoryRepository;

    public ProductPriceServiceImpl(ProductPriceRepository pEntityRepository, @Lazy ProductVariantService pProductVariantService,
                                   ProductPriceHistoryRepository pProductPriceHistoryRepository) {
        super(ProductPrice.class, ProductPriceDTO.class, pEntityRepository);
        this.mvProductVariantService = pProductVariantService;
        this.mvProductPriceHistoryRepository = pProductPriceHistoryRepository;
    }

    @Override
    public ProductPriceDTO getPrice(Long productVariantId) {
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
    public List<ProductPrice> save(ProductDetail productVariant, ProductPriceDTO pPriceDTO) {
        BigDecimal lvRetailPrice = CoreUtils.coalesce(pPriceDTO.getRetailPrice());
        BigDecimal lvWholesalePrice = CoreUtils.coalesce(pPriceDTO.getWholesalePrice());
        BigDecimal lvPurchasePrice = CoreUtils.coalesce(pPriceDTO.getPurchasePrice());
        BigDecimal lvCostPrice = CoreUtils.coalesce(pPriceDTO.getCostPrice());

        List<ProductPrice> lvPrices = new ArrayList<>();
        for (PriceType lvType : PriceType.values()) {
            ProductPrice lvPriceModel = ProductPrice.builder()
                    .state(ProductPrice.STATE_ACTIVE)
                    .productVariant(productVariant)
                    .priceType(lvType)
                    .build();
            if (lvType.equals(PriceType.RTL))
            {
                lvPriceModel.setPriceValue(lvRetailPrice);
                if (!mvEntityRepository.existsByStateAndTypeAndProductVariantId(ProductPrice.STATE_ACTIVE,
                        PriceType.RTL, productVariant.getId())) {
                    lvPrices.add(mvEntityRepository.save(lvPriceModel));
                }
            }
            else if (lvType.equals(PriceType.WHO))
            {
                lvPriceModel.setPriceValue(lvWholesalePrice);
                if (!mvEntityRepository.existsByStateAndTypeAndProductVariantId(ProductPrice.STATE_ACTIVE,
                        PriceType.WHO, productVariant.getId())) {
                    lvPrices.add(mvEntityRepository.save(lvPriceModel));
                }
            }
            else if (lvType.equals(PriceType.CSP))
            {
                lvPriceModel.setPriceValue(lvCostPrice);
                if (!mvEntityRepository.existsByStateAndTypeAndProductVariantId(ProductPrice.STATE_ACTIVE,
                        PriceType.CSP, productVariant.getId())) {
                    lvPrices.add(mvEntityRepository.save(lvPriceModel));
                }
            }
        }

        return lvPrices;
    }

    @Transactional
    @Override
    public ProductPriceDTO update(ProductPriceDTO pRequestPrice, Long pProductVariantId) {
        ProductDetail lvProductVariant = mvProductVariantService.findEntById(pProductVariantId, true);
        return this.updatePrice(lvProductVariant, pRequestPrice);
    }

    @Transactional
    @Override
    public ProductPriceDTO updatePrice(ProductDetail pProductVariant, ProductPriceDTO pRequestPrice) {
        List<ProductPrice> lvPrices = mvEntityRepository.findByVariantId(pProductVariant.getId());
        for (ProductPrice lvPrice : lvPrices) {
            BigDecimal lvPriceCurrent = lvPrice.getPriceValue();
            if (PriceType.RTL.equals(lvPrice.getPriceType())) {
                BigDecimal lvRequestRetailPrice = pRequestPrice.getRetailPrice();
                if (lvPriceCurrent.compareTo(lvRequestRetailPrice) != 0) {
                    handlePriceUpdate(lvPrice, pProductVariant, PriceType.RTL, lvPriceCurrent, lvRequestRetailPrice);
                }
            }
            if (PriceType.WHO.equals(lvPrice.getPriceType())) {
                BigDecimal lvRequestWholesalePrice = pRequestPrice.getWholesalePrice();
                if (lvPriceCurrent.compareTo(lvRequestWholesalePrice) != 0) {
                    handlePriceUpdate(lvPrice, pProductVariant, PriceType.RTL, lvPriceCurrent, lvRequestWholesalePrice);
                }
            }
            if (PriceType.CSP.equals(lvPrice.getPriceType())) {
                BigDecimal lvRequestCostPrice = pRequestPrice.getCostPrice();
                if (lvPriceCurrent.compareTo(lvRequestCostPrice) != 0) {
                    handlePriceUpdate(lvPrice, pProductVariant, PriceType.CSP, lvPriceCurrent, lvRequestCostPrice);
                }
            }
        }

        return getPrice(pProductVariant.getId());
    }

    private void handlePriceUpdate(ProductPrice pPrice, ProductDetail pProductVariant, PriceType pPriceType, BigDecimal currentPrice, BigDecimal newPrice) {
        mvEntityRepository.inactivePrice(pPrice.getId());
        createNewPrice(pProductVariant, pPriceType, newPrice);
        createPriceHistory(pPrice, currentPrice, newPrice);
    }

    private void createNewPrice(ProductDetail productVariant, PriceType priceType, BigDecimal priceValue) {
        mvEntityRepository.save(ProductPrice.builder()
                .state(ProductPrice.STATE_ACTIVE)
                .productVariant(productVariant)
                .priceType(priceType)
                .priceValue(priceValue)
                .build());
    }

    private void createPriceHistory(ProductPrice productPrice, BigDecimal oldPrice, BigDecimal newPrice) {
        mvProductPriceHistoryRepository.save(ProductPriceHistory.builder()
                .productPrice(productPrice)
                .changeType(PriceChangeType.MANUAL)
                .oldPrice(oldPrice)
                .newPrice(newPrice)
                .changeTime(LocalDateTime.now())
                .changedBy(SecurityUtils.getCurrentUser().getEntity())
                .reason("-")
                .build());
    }
}