package com.flowiee.pms.modules.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Entity
@Table(name = "product_detail_temp")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantExim extends BaseEntity implements Serializable {
    @Serial
	static final long serialVersionUID = 1L;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "goods_import_id")
    TicketImport ticketImport;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "goods_export_id")
    TicketExport ticketExport;

    @JsonIgnore
    @ManyToOne
	@JoinColumn(name = "product_variant_id", nullable = false)
	ProductDetail productVariant;

    @Column(name = "quantity", nullable = false)
    Integer quantity;

    @Column(name = "storage_qty")
    Integer storageQty;

    @Column(name = "purchase_price")
    BigDecimal purchasePrice;

    @Column(name = "sell_price")
    BigDecimal sellPrice;

    @Column(name = "note")
    String note;

    @Column(name = "action")
    String action;
}