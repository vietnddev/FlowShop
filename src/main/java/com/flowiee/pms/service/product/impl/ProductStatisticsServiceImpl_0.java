package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.repository.product.ProductDetailRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.product.ProductStatisticsService_0;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductStatisticsServiceImpl_0 extends BaseService implements ProductStatisticsService_0 {
    ProductDetailRepository mvProductVariantRepository;

    @Override
    public Integer countTotalProductsInStorage() {
        return mvProductVariantRepository.countTotalQuantity();
    }

    @Override
    public Integer findProductVariantTotalQtySell(Long productId) {
        Integer totalSellQty = mvProductVariantRepository.findTotalQtySell(productId);
        return totalSellQty != null ? totalSellQty : 0;
    }

    @Override
    public Integer findProductVariantQuantityBySizeOfEachColor(Long productId, Long colorId, Long sizeId) {
        try {
            return mvProductVariantRepository.findQuantityBySizeOfEachColor(productId, colorId, sizeId);
        } catch (RuntimeException ex) {
            System.out.println("productId " + productId + ", colorId " + colorId + ", sizeId " + sizeId);
            throw new RuntimeException("Error finding product variant quantity", ex);
        }
    }
}