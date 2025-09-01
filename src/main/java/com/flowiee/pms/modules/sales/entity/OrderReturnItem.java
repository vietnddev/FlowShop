package com.flowiee.pms.modules.sales.entity;

import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.sales.utils.OrderReturnCondition;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Builder
@Entity
@Table(name = "order_return_item")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderReturnItem extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "order_return_id", nullable = false)
    private OrderReturn orderReturn;

    @Column(name = "item_id")
    private Long itemId;

    private Integer quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "conditions")
    private OrderReturnCondition condition;
}