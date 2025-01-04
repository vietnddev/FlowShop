package com.flowiee.pms.entity.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
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
    BigDecimal purchasePrice;

    @Column(name = "cost_price")
    BigDecimal costPrice;

    @Column(name = "retail_price", nullable = false)
    BigDecimal retailPrice;

    @Column(name = "retail_price_discount")
    BigDecimal retailPriceDiscount;

    @Column(name = "wholesale_price", nullable = false)
    BigDecimal wholesalePrice;

    @Column(name = "wholesale_price_discount")
    BigDecimal wholesalePriceDiscount;

    @Column(name = "note")
    String note;

    @Column(name = "state", nullable = false)
    String state;
}