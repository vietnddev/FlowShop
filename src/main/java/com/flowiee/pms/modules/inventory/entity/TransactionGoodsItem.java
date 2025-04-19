package com.flowiee.pms.modules.inventory.entity;

import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.product.entity.Material;
import com.flowiee.pms.modules.product.entity.ProductDetail;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Entity
@Table(name = "transaction_goods_item")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionGoodsItem extends BaseEntity implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_goods_id", nullable = false)
    private TransactionGoods transactionGoods;

    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    private ProductDetail productVariant;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;

    @Column(name = "unit_cost")
    private BigDecimal unitCost;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "note")
    private String note;
}