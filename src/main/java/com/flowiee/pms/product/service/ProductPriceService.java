package com.flowiee.pms.product.service;

import com.flowiee.pms.product.dto.ProductPriceDTO;
import com.flowiee.pms.product.dto.ProductVariantDTO;
import com.flowiee.pms.product.entity.ProductDetail;
import com.flowiee.pms.product.entity.ProductPrice;

import java.util.List;

public interface ProductPriceService {
    List<ProductPrice> save(ProductDetail productVariant, ProductPriceDTO pPriceDTO);

    ProductPriceDTO updatePrice(Long pProductVariantId, ProductPriceDTO pRequestPrice);

    ProductPriceDTO updatePrice(ProductDetail pProductVariant, ProductPriceDTO pRequestPrice);

    List<ProductPriceDTO> findPresentPrices(List<Long> productVariantIds);

    ProductPriceDTO findPresentPrice(Long productVariantId);

    ProductVariantDTO assignPriceInfo(ProductVariantDTO pDto, List<ProductPrice> pProductPrice);
}