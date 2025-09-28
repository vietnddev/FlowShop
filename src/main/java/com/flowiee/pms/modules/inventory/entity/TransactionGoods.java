package com.flowiee.pms.modules.inventory.entity;

import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.inventory.enums.TransactionGoodsStatus;
import com.flowiee.pms.modules.inventory.enums.TransactionGoodsType;
import com.flowiee.pms.modules.sales.entity.Order;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Table(name = "transaction_goods")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionGoods extends BaseEntity implements Serializable {
    @Column(name = "transaction_code", nullable = false, length = 30)
    private String code;

    @Column(name = "transaction_source", length = 30)
    private String source;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", length = 20)
    private TransactionGoodsType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", length = 30)
    private TransactionGoodsStatus transactionStatus;

    @Column(name = "description")
    private String description;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @Column(name = "confirmed_by")
    private String confirmedBy;

    @Column(name = "confirmed_time")
    private LocalDateTime confirmedTime;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_time")
    private LocalDateTime approvedTime;

    @Column(name = "rejected_by")
    private String rejectedBy;

    @Column(name = "rejected_time")
    private LocalDateTime rejectedTime;

    @Column(name = "rejected_reason_note")
    private String rejectedReason;

    @Column(name = "request_note")
    private String requestNote;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "source_type")
    private String sourceType;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Storage warehouse;

    @OneToMany(mappedBy = "transactionGoods", fetch = FetchType.LAZY)
    private List<TransactionGoodsItem> items;
}