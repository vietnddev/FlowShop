package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.inventory.entity.ProductPrice;
import com.flowiee.pms.modules.inventory.dto.ProductPriceDTO;
import com.flowiee.pms.modules.inventory.entity.ProductPriceHistory;
import com.flowiee.pms.modules.inventory.enums.PriceChangeType;
import com.flowiee.pms.modules.inventory.enums.PriceType;
import com.flowiee.pms.modules.inventory.repository.ProductPriceRepository;
import com.flowiee.pms.modules.inventory.service.ProductPriceService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductPriceServiceImpl extends BaseService<ProductPrice, ProductPriceDTO, ProductPriceRepository> implements ProductPriceService {
    public ProductPriceServiceImpl(ProductPriceRepository pEntityRepository) {
        super(ProductPrice.class, ProductPriceDTO.class, pEntityRepository);
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
    public ProductPriceDTO updatePrice(ProductDetail pProductVariant, ProductPriceDTO pRequestPrice) {
        ProductPriceDTO lvPriceUpdated = new ProductPriceDTO();

        List<ProductPrice> lvCurrentPrices = mvEntityRepository.findPresentPrices(pProductVariant.getId());
        if (CollectionUtils.isNotEmpty(lvCurrentPrices)) {
            for (ProductPrice lvCPrice : lvCurrentPrices) {
                if (isPriceChanged(lvCPrice, pRequestPrice)) {
                    lvCPrice.setState(ProductPrice.STATE_INACTIVE);
                    mvEntityRepository.save(lvCPrice);

                    this.save(pProductVariant, pRequestPrice);
                }
            }
            lvPriceUpdated = new ProductPriceDTO();
            lvPriceUpdated.setRetailPrice(pRequestPrice.getRetailPrice());
            lvPriceUpdated.setCostPrice(pRequestPrice.getCostPrice());
            lvPriceUpdated.setLastUpdatedAt(LocalDateTime.now());
        } else {
            lvPriceUpdated = extractPrice(this.save(pProductVariant, pRequestPrice));
        }


        ProductPriceHistory history = new ProductPriceHistory();
        //history.setProductPrice(price);
        history.setChangeType(PriceChangeType.MANUAL);
        //history.setOldValue(price.getRetailPrice());
        //history.setNewValue(newPrice);
        //history.setChangedBy(changer);
        history.setChangeTime(LocalDateTime.now());
        //history.setReason(reason);
        //historyRepo.save(history);

        // Cập nhật giá mới
        //price.setRetailPrice(newPrice);
        //priceRepo.save(price);

        return lvPriceUpdated;
    }

    @Override
    public List<ProductPriceDTO> findPresentPrices(List<Long> pProductVariantIds) {
        if (CollectionUtils.isEmpty(pProductVariantIds)) {
            return List.of();
        }

        List<ProductPrice> lvPriceList = new ArrayList<>();
        int batchSize = 1000; // Số lượng tối đa trong một truy vấn
        for (int i = 0; i < pProductVariantIds.size(); i += batchSize) {
            List<Long> batch = new ArrayList<>(pProductVariantIds.subList(i, Math.min(i + batchSize, pProductVariantIds.size())));
            lvPriceList.addAll(mvEntityRepository.findPresentPrices(batch));
        }

        return convertDTOs(lvPriceList);
    }

    @Override
    public ProductPriceDTO findPresentPrice(Long productVariantId) {
        ProductPrice lvPrice = mvEntityRepository.findPricePresent(productVariantId);
        return lvPrice != null ? ProductPriceDTO.toDTO(lvPrice) : new ProductPriceDTO();
    }

    @Override
    public ProductVariantDTO assignPriceInfo(ProductVariantDTO pDto, List<ProductPrice> pProductPrice) {
        if (pDto != null) {
            if (pProductPrice != null) {
                pDto.setPrice(extractPrice(pProductPrice));
            } else {
                pDto.setPrice(new ProductPriceDTO());
            }
        }
        return pDto;
    }

    private ProductPriceDTO extractPrice(List<ProductPrice> pProductPrices) {
        ProductPriceDTO lvPriceDto = new ProductPriceDTO();
        for (ProductPrice lvPrice : pProductPrices)
        {
            PriceType lvType = lvPrice.getPriceType();
            BigDecimal lvValue = lvPrice.getPriceValue();
            LocalDateTime lvLastUpdated = lvPrice.getLastUpdatedAt();

            if (PriceType.RTL.equals(lvType)) {
                lvPriceDto.setRetailPrice(lvValue);
                lvPriceDto.setLastUpdatedAt(lvLastUpdated);
            } else if (PriceType.WHO.equals(lvType)) {
                lvPriceDto.setWholesalePrice(lvValue);
                lvPriceDto.setLastUpdatedAt(lvLastUpdated);
            } else if (PriceType.CSP.equals(lvType)) {
                lvPriceDto.setCostPrice(lvValue);
                lvPriceDto.setLastUpdatedAt(lvLastUpdated);
            }
        }
        return lvPriceDto;
    }

    private boolean isPriceChanged(ProductPrice pCPrice, ProductPriceDTO pRPrice) {
        //Current price
        PriceType lvCPriceType = pCPrice.getPriceType();
        BigDecimal lvCPriceValue = pCPrice.getPriceValue();

        //Request price
        BigDecimal lvRRetailPrice = CoreUtils.coalesce(pRPrice.getRetailPrice());
        BigDecimal lvRWholesalePrice = CoreUtils.coalesce(pRPrice.getWholesalePrice());
        BigDecimal lvRPurchasePrice = CoreUtils.coalesce(pRPrice.getPurchasePrice());
        BigDecimal lvRCostPrice = CoreUtils.coalesce(pRPrice.getCostPrice());

        boolean isChanged = false;
        if (PriceType.RTL.equals(lvCPriceType) && lvCPriceValue.compareTo(lvRRetailPrice) != 0) {
            isChanged = true;
        } else if (PriceType.WHO.equals(lvCPriceType) && lvCPriceValue.compareTo(lvRWholesalePrice) != 0) {
            isChanged = true;
        } else if (PriceType.CSP.equals(lvCPriceType) && lvCPriceValue.compareTo(lvRCostPrice) != 0) {
            isChanged = true;
        }

        return isChanged;
    }
}