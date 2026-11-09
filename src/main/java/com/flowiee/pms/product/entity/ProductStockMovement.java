package com.flowiee.pms.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.product.enums.StockMovementType;
import com.flowiee.pms.product.enums.StockQuantityType;
import com.flowiee.pms.shared.base.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "product_stock_movement")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductStockMovement extends BaseEntity implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_id", nullable = false)
    private ProductInventory productInventory;

    @Enumerated(EnumType.STRING)
    @Column(name = "stock_movement_type", nullable = false, length = 30)
    StockMovementType movementType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    StockQuantityType quantityType;

    @Column(name = "quantity_change", nullable = false)
    Integer quantityChange;

    @Column(name = "quantity_before", nullable = false)
    Integer quantityBefore;

    @Column(name = "quantity_after", nullable = false)
    Integer quantityAfter;

    @Column(name = "reference_type", length = 30)
    String referenceType;

    @Column(name = "reference_id")
    Long referenceId; //referenceType = ORDER ; referenceId (orderId) = 12345

    @Column(name = "note", length = 500)
    String note;
}