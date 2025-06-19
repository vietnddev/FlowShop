package com.flowiee.pms.common.utils;

import com.flowiee.pms.modules.inventory.entity.ProductPrice;
import com.flowiee.pms.modules.inventory.enums.PriceType;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

public class PriceUtils {
    public static BigDecimal getPriceValue(List<ProductPrice> pPrices, PriceType pPriceType) {
        if (CollectionUtils.isEmpty(pPrices)) {
            return BigDecimal.ZERO;
        }

        for (ProductPrice lvPrice : pPrices) {
            if (pPriceType.equals(lvPrice.getPriceType())) {
                return lvPrice.getPriceValue();
            }
        }

        return BigDecimal.ZERO;
    }
}