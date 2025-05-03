package com.flowiee.pms.modules.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "product_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductHistory extends BaseEntity implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    ProductDetail productDetail;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_attribute_id")
    ProductAttribute productAttribute;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "field", nullable = false)
    String field;

    @Lob
    @Column(name = "old_value", nullable = false, columnDefinition = "TEXT")
    String oldValue;

    @Lob
    @Column(name = "new_value", nullable = false, columnDefinition = "TEXT")
    String newValue;

    @Transient
    Long productId;

    @Transient
    Long productVariantId;

    @Transient
    Long productAttributeId;

	@Override
	public String toString() {
		return "ProductHistory [id=" + super.id + ", title=" + title + ", fieldName=" + field + ", oldValue=" + oldValue + ", newValue=" + newValue + "]";
	}
}