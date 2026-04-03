package com.flowiee.pms.product.service;

import com.flowiee.pms.product.dto.ProductPriceDTO;
import com.flowiee.pms.product.entity.ProductDetail;
import com.flowiee.pms.product.entity.ProductPrice;
import com.flowiee.pms.shared.base.UpdateService;

import java.util.List;

public interface ProductPriceService extends UpdateService<ProductPriceDTO> {
    ProductPriceDTO getPrice(Long productVariantId);

    List<ProductPrice> save(ProductDetail productVariant, ProductPriceDTO pPriceDTO);

    ProductPriceDTO updatePrice(ProductDetail pProductVariant, ProductPriceDTO pRequestPrice);
}