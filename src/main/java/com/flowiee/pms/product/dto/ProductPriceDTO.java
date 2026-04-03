package com.flowiee.pms.product.dto;

import com.flowiee.pms.product.entity.ProductPrice;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class ProductPriceDTO implements Serializable {
    private Long productVariantId;
    private BigDecimal retailPrice;
    private BigDecimal retailPriceDiscount;
    private BigDecimal wholesalePrice;
    private BigDecimal wholesalePriceDiscount;
    private BigDecimal purchasePrice;
    private BigDecimal costPrice;
    private LocalDateTime lastUpdatedAt;
    private String note;
    private String state;

    public ProductPriceDTO() {
        this.retailPrice = BigDecimal.ZERO;
        this.retailPriceDiscount = BigDecimal.ZERO;
        this.wholesalePrice = BigDecimal.ZERO;
        this.wholesalePriceDiscount = BigDecimal.ZERO;
        this.purchasePrice = BigDecimal.ZERO;
        this.costPrice = BigDecimal.ZERO;
    }

    public ProductPriceDTO(Long productVariantId, BigDecimal retailPrice, BigDecimal wholesalePrice, BigDecimal costPrice) {
        this.productVariantId = productVariantId;
        this.retailPrice = retailPrice;
        this.retailPriceDiscount = retailPrice;
        this.wholesalePrice = wholesalePrice;
        this.wholesalePriceDiscount = wholesalePrice;
        this.costPrice = costPrice;
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