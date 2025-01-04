package com.flowiee.pms.entity.sales;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ledger")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ledger extends BaseEntity implements Serializable {
    @Column(name = "year", nullable = false)
    int year;

    @Column(name = "month", nullable = false)
    int month;

    @Column(name = "begin_balance", nullable = false)
    BigDecimal beginBal;

    @Column(name = "total_receipt", nullable = false)
    BigDecimal totalReceipt;

    @Column(name = "total_payment", nullable = false)
    BigDecimal totalPayment;

    @Column(name = "end_balance", nullable = false)
    BigDecimal endBal;
}