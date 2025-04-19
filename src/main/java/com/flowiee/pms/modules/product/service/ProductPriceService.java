package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.modules.product.dto.ProductPriceDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ProductPriceService {
    String updateProductPrice(Long variantId, BigDecimal originalPrice, BigDecimal discountPrice);

    List<ProductPriceDTO> findPresentPrices(List<Long> productVariantIds);
}