package com.flowiee.pms.modules.sales.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.common.enumeration.LoyaltyTransactionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "loyalty_transaction")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoyaltyTransaction extends BaseEntity implements Serializable {
    @OneToOne(mappedBy = "loyaltyTransaction")
    Order order;

    @ManyToOne
    @JoinColumn(name = "customer_Id", nullable = false)
    Customer customer;

    @ManyToOne
    @JoinColumn(name = "loyalty_program_Id", nullable = false)
    LoyaltyProgram loyaltyProgram;

    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    LoyaltyTransactionType transactionType; // ACCUMULATE or REDEEM

    @Column(name = "points", nullable = false)
    Integer points;

    @Column(name = "transaction_date", nullable = false)
    LocalDateTime transactionDate;

    @Column(name = "remark")
    String remark;
}