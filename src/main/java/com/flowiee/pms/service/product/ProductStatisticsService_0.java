package com.flowiee.pms.service.product;

public interface ProductStatisticsService_0 {
    Integer countTotalProductsInStorage();

    Integer findProductVariantTotalQtySell(Long productId);

    Integer findProductVariantQuantityBySizeOfEachColor(Long productId, Long colorId, Long sizeId);
}