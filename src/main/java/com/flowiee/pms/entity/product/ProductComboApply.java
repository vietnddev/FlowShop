package com.flowiee.pms.entity.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
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