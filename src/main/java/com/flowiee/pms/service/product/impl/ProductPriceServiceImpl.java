package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.product.ProductPriceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductPriceServiceImpl extends BaseService implements ProductPriceService {

    @Transactional
    @Override
    public String updateProductPrice(Long variantId, BigDecimal pOriginalPrice, BigDecimal pDiscountPrice) {
        return "";
    }
}