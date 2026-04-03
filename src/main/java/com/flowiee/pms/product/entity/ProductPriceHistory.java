package com.flowiee.pms.product.entity;

import com.flowiee.pms.product.enums.PriceChangeType;
import com.flowiee.pms.modules.staff.entity.Account;
import javax.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "product_price_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductPriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_price_id", nullable = false)
    private ProductPrice productPrice;

    @Enumerated(EnumType.STRING)
    private PriceChangeType changeType;

    @Column(name = "old_price", nullable = false)
    private BigDecimal oldPrice;

    @Column(name = "new_price", nullable = false)
    private BigDecimal newPrice;

    @Column(name = "change_time", nullable = false)
    private LocalDateTime changeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private Account changedBy;

    @Column(name = "reason")
    private String reason;
}