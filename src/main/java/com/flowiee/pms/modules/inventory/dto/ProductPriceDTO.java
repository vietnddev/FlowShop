package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.modules.inventory.entity.Product;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.inventory.entity.ProductPrice;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class ProductPriceDTO implements Serializable {
    Product productBase;//Change to DTO or remove in next version
    ProductDetail productVariant;//Change to DTO or remove in next version
    BigDecimal retailPrice;
    BigDecimal retailPriceDiscount;
    BigDecimal wholesalePrice;
    BigDecimal wholesalePriceDiscount;
    BigDecimal purchasePrice;
    BigDecimal costPrice;
    LocalDateTime lastUpdatedAt;
    String note;
    String state;

    public ProductPriceDTO() {
        this.retailPrice = BigDecimal.ZERO;
        this.retailPriceDiscount = BigDecimal.ZERO;
        this.wholesalePrice = BigDecimal.ZERO;
        this.wholesalePriceDiscount = BigDecimal.ZERO;
        this.purchasePrice = BigDecimal.ZERO;
        this.costPrice = BigDecimal.ZERO;
    }

    public static ProductPriceDTO toDTO(ProductPrice productPrice) {
        return ProductPriceDTO.builder()
                //.retailPrice(productPrice.getRetailPrice())
                //.retailPriceDiscount(productPrice.getRetailPriceDiscount())
                //.wholesalePrice(productPrice.getWholesalePrice())
                //.wholesalePriceDiscount(productPrice.getWholesalePriceDiscount())
                //.costPrice(productPrice.getCostPrice())
                //.purchasePrice(productPrice.getPurchasePrice())
                .lastUpdatedAt(productPrice.getLastUpdatedAt())
                .build();
    }
}