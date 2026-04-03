package com.flowiee.pms.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.shared.base.BaseEntity;
import com.flowiee.pms.product.enums.DiscountType;
import com.flowiee.pms.product.enums.PriceType;
import com.flowiee.pms.modules.system.entity.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    @JsonIgnore
    @OneToMany(mappedBy = "productPrice", fetch = FetchType.LAZY)
    List<ProductPriceHistory> productPriceHistoryList;
}