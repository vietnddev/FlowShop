package com.flowiee.pms.product.service;

import com.flowiee.pms.product.dto.ProductPriceDTO;
import com.flowiee.pms.shared.base.CreateService;

import java.util.List;
import java.util.Map;

public interface ProductPriceService extends CreateService<ProductPriceDTO> {
    ProductPriceDTO getPrices(Long productVariantId);

    Map<Long, ProductPriceDTO> getPrices(List<Long> productVariantId);

    ProductPriceDTO updatePrice(Long pProductVariantId, ProductPriceDTO pRequestPrice);
}