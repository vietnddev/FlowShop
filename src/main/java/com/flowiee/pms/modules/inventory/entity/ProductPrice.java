package com.flowiee.pms.modules.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.inventory.enums.DiscountType;
import com.flowiee.pms.modules.inventory.enums.PriceType;
import com.flowiee.pms.modules.system.entity.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    ProductDetail productVariant;

//    @Column(name = "purchase_price")
//    BigDecimal purchasePrice = BigDecimal.ZERO;
//
//    @Column(name = "cost_price")
//    BigDecimal costPrice = BigDecimal.ZERO;
//
//    @Column(name = "retail_price")
//    BigDecimal retailPrice = BigDecimal.ZERO;
//
//    @Column(name = "retail_price_discount")
//    BigDecimal retailPriceDiscount = BigDecimal.ZERO;
//
//    @Column(name = "wholesale_price")
//    BigDecimal wholesalePrice = BigDecimal.ZERO;
//
//    @Column(name = "wholesale_price_discount")
//    BigDecimal wholesalePriceDiscount = BigDecimal.ZERO;

    @Column(name = "price_value")
    BigDecimal priceValue = BigDecimal.ZERO; // new

    @Column(name = "applied_value")
    BigDecimal appliedValue; // new

    @Enumerated(EnumType.STRING)
    @Column(name = "price_type")
    PriceType priceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    DiscountType discountType; // new

    @Column(name = "discount_value")
    BigDecimal discountValue; // new

    @Column(name = "effective_from")
    LocalDateTime effectiveFrom; // new

    @Column(name = "effective_to")
    LocalDateTime effectiveTo; // new

    @ManyToOne
    @JoinColumn(name = "applicable_customer_group")
    Category applicableCustomerGroup; // Null = áp dụng chung

    @Column(name = "note")
    String note;

    @Column(name = "state", nullable = false)
    String state;
}