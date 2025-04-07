package com.flowiee.pms.service.product;

import com.flowiee.pms.entity.product.ProductPrice;
import com.flowiee.pms.model.dto.ProductPriceDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ProductPriceService {
    String updateProductPrice(Long variantId, BigDecimal originalPrice, BigDecimal discountPrice);

    List<ProductPriceDTO> findPresentPrices(List<Long> productVariantIds);
}