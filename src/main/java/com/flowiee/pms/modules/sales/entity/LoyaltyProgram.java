package com.flowiee.pms.modules.sales.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Table(name = "loyalty_program")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoyaltyProgram extends BaseEntity implements Serializable {
    @Column(name = "name", nullable = false, unique = true)
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "min_order_value")
    BigDecimal minOrderValue;

    @Column(name = "max_order_value")
    BigDecimal maxOrderValue;

    @Column(name = "point_conversion_rate", nullable = false)
    BigDecimal pointConversionRate;

    @Column(name = "start_date", nullable = false)
    LocalDate startDate;

    @Column(name = "end_date")
    LocalDate endDate;

    @Column(name = "is_active", nullable = false)
    boolean isActive;

    @Column(name = "is_default", nullable = false)
    Boolean isDefault;

    @JsonIgnore
    @OneToMany(mappedBy = "loyaltyProgram", fetch = FetchType.LAZY)
    List<LoyaltyTransaction> loyaltyTransactionList;

    public boolean isExpired() {
        return LocalDate.now().isAfter(endDate);
    }
}