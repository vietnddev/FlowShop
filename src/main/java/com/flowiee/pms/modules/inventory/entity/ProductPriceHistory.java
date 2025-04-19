package com.flowiee.pms.modules.inventory.entity;

import com.flowiee.pms.modules.inventory.enums.PriceChangeType;
import com.flowiee.pms.modules.staff.entity.Account;
import jakarta.persistence.*;
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
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductDetail productVariant;

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
    private Account changedBy; // Người thay đổi giá

    @Column(name = "reason")
    private String reason;
}