package com.flowiee.pms.entity.sales;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.BaseEntity;
import com.flowiee.pms.entity.category.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Entity
@Table(name = "ledger_transaction")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LedgerTransaction extends BaseEntity implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    @Column(name = "tran_index", nullable = false)
    Long tranIndex;

    @Column(name = "tran_code", nullable = false)
    String tranCode;

    @Column(name = "tran_type", nullable = false)
    String tranType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_object_id", nullable = false)
    Category groupObject;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tran_content_id", nullable = false)
    Category tranContent;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id", nullable = false)
    Category paymentMethod;

    @Column(name = "from_to_name", nullable = false)
    String fromToName;

    @Column(name = "amount", nullable = false)
    BigDecimal amount;

    @Column(name = "description")
    String description;

    @Column(name = "status")
    String status;

    @Transient
    String tranContentName;

    @Transient
    String groupObjectName;

    @Transient
    String paymentMethodName;
}