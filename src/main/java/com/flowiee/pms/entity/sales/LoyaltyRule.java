package com.flowiee.pms.entity.sales;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Entity
@Table(name = "loyalty_rule")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LoyaltyRule extends BaseEntity implements Serializable {
    @ManyToOne
    @JoinColumn(name = "loyalty_program_id", nullable = false)
    private LoyaltyProgram loyaltyProgram;

    @Column(name = "min_order_value", nullable = false)
    private BigDecimal minOrderValue;

    @Column(name = "max_order_value")
    private BigDecimal maxOrderValue;

    @Column(name = "point_conversion_rate", nullable = false)
    private BigDecimal pointConversionRate;

    @Column(name = "currency_unit")
    private String currencyUnit;
}