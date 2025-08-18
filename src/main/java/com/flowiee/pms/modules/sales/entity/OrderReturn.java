package com.flowiee.pms.modules.sales.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.sales.utils.OrderRefundMethod;
import com.flowiee.pms.modules.sales.utils.OrderReturnStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Table(name = "order_return")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderReturn extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "returns_code", unique = true, nullable = false)
    private String returnsCode;

    @Column(name = "reason")
    private String reason;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_method")
    private OrderRefundMethod refundMethod;

    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    @Column(name = "is_refunded")
    private Boolean isRefunded;

    @Enumerated(EnumType.STRING)
    private OrderReturnStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "orderReturn", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<OrderReturnItem> orderReturnItemList;
}