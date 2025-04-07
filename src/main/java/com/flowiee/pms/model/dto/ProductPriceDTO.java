package com.flowiee.pms.model.dto;

import com.flowiee.pms.entity.product.Product;
import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.entity.product.ProductPrice;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class ProductPriceDTO implements Serializable {
    Product productBase;
    ProductDetail productVariant;
    BigDecimal retailPrice;
    BigDecimal retailPriceDiscount;
    BigDecimal wholesalePrice;
    BigDecimal wholesalePriceDiscount;
    BigDecimal purchasePrice;
    BigDecimal costPrice;
    LocalDateTime lastUpdated;
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
                .retailPrice(productPrice.getRetailPrice())
                .retailPriceDiscount(productPrice.getRetailPriceDiscount())
                .wholesalePrice(productPrice.getWholesalePrice())
                .wholesalePriceDiscount(productPrice.getWholesalePriceDiscount())
                .lastUpdated(productPrice.getLastUpdatedAt())
                .build();
    }
}