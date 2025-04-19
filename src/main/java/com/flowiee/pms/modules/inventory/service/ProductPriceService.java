package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.modules.inventory.dto.ProductPriceDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ProductPriceService {
    String updatePrice(Long variantId, BigDecimal originalPrice, BigDecimal discountPrice);

    List<ProductPriceDTO> findPresentPrices(List<Long> productVariantIds);

    ProductPriceDTO findPresentPrice(Long productVariantId);
}