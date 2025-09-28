package com.flowiee.pms.modules.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;

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

    @ManyToOne
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