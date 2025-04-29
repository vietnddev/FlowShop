package com.flowiee.pms.modules.sales.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Entity
@Table(name = "customer_debt")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDebt extends BaseEntity implements Serializable {
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    @Column(name = "debt_amount", nullable = false)
    BigDecimal debtAmount;

    @Column(name = "due_date", nullable = false)
    LocalDate dueDate;

    @Column(name = "status", nullable = false)
    String status;

    @Column(name = "note")
    String note;
}