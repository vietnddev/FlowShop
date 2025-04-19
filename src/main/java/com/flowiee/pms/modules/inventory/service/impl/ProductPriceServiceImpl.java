package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.modules.inventory.entity.ProductPrice;
import com.flowiee.pms.modules.inventory.dto.ProductPriceDTO;
import com.flowiee.pms.modules.inventory.entity.ProductPriceHistory;
import com.flowiee.pms.modules.inventory.enums.PriceChangeType;
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

    @Transactional
    @Override
    public String updatePrice(Long variantId, BigDecimal pOriginalPrice, BigDecimal pDiscountPrice) {
        ProductPrice price = mvEntityRepository.findById(0l).orElseThrow();

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

        return "";
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
        ProductPrice lvPrice = mvEntityRepository.findPricePresent(null, productVariantId);
        return lvPrice != null ? ProductPriceDTO.toDTO(lvPrice) : new ProductPriceDTO();
    }
}