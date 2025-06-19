package com.flowiee.pms.modules.inventory.service;

public interface ProductStatisticsService_0 {
    Integer countTotalProductsInStorage();

    Integer findProductVariantTotalQtySell(Long productId);
}