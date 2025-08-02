package com.flowiee.pms.modules.sales.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.system.entity.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Entity
@Table(name = "ledger_receipt")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LedgerReceipt extends BaseEntity implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    @Column(name = "receipt_index", nullable = false)
    Integer receiptIndex;

    @Column(name = "receipt_code", length = 10, nullable = false)
    String receiptCode;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "receiver_group_id", nullable = false)
    Category receiverGroup;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "receipt_type_id", nullable = false)
    Category receiptType;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "payment_method_id", nullable = false)
    Category paymentMethod;

    @Column(name = "receiver_name", length = 50, nullable = false)
    String receiverName;

    @Column(name = "receipt_amount", nullable = false)
    BigDecimal receiptAmount;

    @Column(name = "description")
    String description;

    @Column(name = "status", nullable = false)
    String status;

    @Transient
    String receiptTypeName;

    @Transient
    String receiverGroupName;

    @Transient
    String paymentMethodName;
}