package com.flowiee.pms.modules.sales.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Entity
@Table(name = "order_cart_items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Items extends BaseEntity implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

	@ManyToOne
    @JoinColumn(name = "product_detail_id", nullable = false)
    ProductDetail productDetail;

    @Column(name = "quantity")
    int quantity;

    @Column(name = "note")
    String note;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    OrderCart orderCart;

    @Column(name = "price_type", nullable = false)
    String priceType;//S or L

    @Column(name = "price", nullable = false)
    BigDecimal price;

    @Column(name = "price_original", nullable = false)
    BigDecimal priceOriginal;

    @Column(name = "extra_discount")
    BigDecimal extraDiscount;

    @Transient
    Long productVariantId;

	@Override
	public String toString() {
		return "Items [id=" + super.id + ", quantity=" + quantity + ", orderCart=" + orderCart + "]";
	}
}