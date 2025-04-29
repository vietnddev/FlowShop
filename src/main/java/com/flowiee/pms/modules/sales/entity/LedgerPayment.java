package com.flowiee.pms.modules.sales.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.category.entity.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ledger_payment")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LedgerPayment extends BaseEntity implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    @Column(name = "payment_index", nullable = false)
    Integer paymentIndex;

    @Column(name = "payment_code", nullable = false)
    String paymentCode;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_group_id", nullable = false)
    Category payerGroup;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_type_id", nullable = false)
    Category paymentType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id", nullable = false)
    Category paymentMethod;

    @Column(name = "payer_name", nullable = false)
    String payerName;

    @Column(name = "payment_amount", nullable = false)
    BigDecimal paymentAmount;

    @Column(name = "description")
    String description;

    @Column(name = "status")
    String status;

    @Transient
    String paymentTypeName;

    @Transient
    String payerGroupName;

    @Transient
    String paymentMethodName;
}
