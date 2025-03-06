package com.flowiee.pms.service.product;

import com.flowiee.pms.entity.product.ProductPrice;

import java.math.BigDecimal;
import java.util.List;

public interface ProductPriceService {
    String updateProductPrice(Long variantId, BigDecimal originalPrice, BigDecimal discountPrice);

    List<ProductPrice> findPresentPrices(List<Long> productVariantIds);
}