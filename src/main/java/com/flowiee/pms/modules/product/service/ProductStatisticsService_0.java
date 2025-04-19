package com.flowiee.pms.modules.product.service;

public interface ProductStatisticsService_0 {
    Integer countTotalProductsInStorage();

    Integer findProductVariantTotalQtySell(Long productId);
}