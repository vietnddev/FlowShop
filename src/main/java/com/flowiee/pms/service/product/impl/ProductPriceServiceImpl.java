package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.entity.product.ProductPrice;
import com.flowiee.pms.repository.product.ProductPriceRepository;
import com.flowiee.pms.service.product.ProductPriceService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductPriceServiceImpl extends BaseService implements ProductPriceService {
    private final ProductPriceRepository productPriceRepository;

    @Transactional
    @Override
    public String updateProductPrice(Long variantId, BigDecimal pOriginalPrice, BigDecimal pDiscountPrice) {
        return "";
    }

    @Override
    public List<ProductPrice> findPresentPrices(List<Long> pProductVariantIds) {
        if (CollectionUtils.isEmpty(pProductVariantIds)) {
            return List.of();
        }

        List<ProductPrice> lvPriceList = new ArrayList<>();
        int batchSize = 1000; // Số lượng tối đa trong một truy vấn
        for (int i = 0; i < pProductVariantIds.size(); i += batchSize) {
            List<Long> batch = pProductVariantIds.subList(i, Math.min(i + batchSize, pProductVariantIds.size()));
            lvPriceList.addAll(productPriceRepository.findPresentPrices(batch));
        }

        return lvPriceList;
    }
}