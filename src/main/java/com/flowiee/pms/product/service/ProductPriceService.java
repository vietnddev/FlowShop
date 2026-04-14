package com.flowiee.pms.product.service;

import com.flowiee.pms.product.dto.ProductPriceDTO;
import com.flowiee.pms.shared.base.CreateService;

public interface ProductPriceService extends CreateService<ProductPriceDTO> {
    ProductPriceDTO getPrice(Long productVariantId);

    ProductPriceDTO updatePrice(Long pProductVariantId, ProductPriceDTO pRequestPrice);
}