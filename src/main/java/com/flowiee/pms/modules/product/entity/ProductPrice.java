package com.flowiee.pms.modules.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Entity
@Table(name = "product_price")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPrice extends BaseEntity implements Serializable {
    public static final String STATE_ACTIVE = "A";
    public static final String STATE_INACTIVE = "I";

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_base_id")
    Product productBase;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    ProductDetail productVariant;

    @Column(name = "purchase_price")
    BigDecimal purchasePrice = BigDecimal.ZERO;

    @Column(name = "cost_price")
    BigDecimal costPrice = BigDecimal.ZERO;

    @Column(name = "retail_price", nullable = false)
    BigDecimal retailPrice = BigDecimal.ZERO;

    @Column(name = "retail_price_discount")
    BigDecimal retailPriceDiscount = BigDecimal.ZERO;

    @Column(name = "wholesale_price", nullable = false)
    BigDecimal wholesalePrice = BigDecimal.ZERO;

    @Column(name = "wholesale_price_discount")
    BigDecimal wholesalePriceDiscount = BigDecimal.ZERO;

    @Column(name = "note")
    String note;

    @Column(name = "state", nullable = false)
    String state;
}