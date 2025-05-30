package com.flowiee.pms.modules.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "product_combo_apply")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductComboApply extends BaseEntity implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    @Column(name = "product_variant_id", nullable = false)
    Long productVariantId;

    @Column(name = "combo_id", nullable = false)
    Long comboId;

    @Override
    public String toString() {
        return "ProductComboApply [id=" + this.id + ", productVariantId=" + productVariantId + ", comboId=" + comboId + "]";
    }
}