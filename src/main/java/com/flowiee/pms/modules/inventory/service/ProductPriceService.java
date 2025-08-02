package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.modules.inventory.dto.ProductPriceDTO;
import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.inventory.entity.ProductPrice;

import java.util.List;

public interface ProductPriceService {
    List<ProductPrice> save(ProductDetail productVariant, ProductPriceDTO pPriceDTO);

    ProductPriceDTO updatePrice(ProductDetail pProductVariant, ProductPriceDTO pRequestPrice);

    List<ProductPriceDTO> findPresentPrices(List<Long> productVariantIds);

    ProductPriceDTO findPresentPrice(Long productVariantId);

    ProductVariantDTO assignPriceInfo(ProductVariantDTO pDto, List<ProductPrice> pProductPrice);
}