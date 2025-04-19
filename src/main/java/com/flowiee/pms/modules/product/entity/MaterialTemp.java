package com.flowiee.pms.modules.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;

import com.flowiee.pms.modules.inventory.entity.TicketImport;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Entity
@Table(name = "material_temp")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialTemp extends BaseEntity implements Serializable {
    @Serial
	static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_import_id", nullable = false)
    TicketImport ticketImport;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "material_id", nullable = false)
	Material material;

    @Column(name = "quantity", nullable = false)
    Integer quantity;

    @Column(name = "storage_qty")
    Integer storageQty;

    @Column(name = "purchase_price")
    BigDecimal purchasePrice;

    @Column(name = "note")
    String note;
}