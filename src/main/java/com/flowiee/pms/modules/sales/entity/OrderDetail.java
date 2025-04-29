package com.flowiee.pms.modules.sales.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.product.entity.ProductDetail;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Builder
@Entity
@Table(name = "order_detail")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetail extends BaseEntity implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	@JsonIgnore
	@JsonIgnoreProperties("listDonHangChiTiet")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	Order order;

	@JsonIgnore
	@JsonIgnoreProperties("listDonHangChiTiet")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_variant_id", nullable = false)
	ProductDetail productDetail;

	@Column(name = "is_gift")
	Boolean isGift;

	@Column(name = "warehouse_code")
	String warehouseCode;

	@Column(name = "price_type", nullable = false)
	String priceType;//S or L

	@Column(name = "price", nullable = false)
	BigDecimal price;

	@Column(name = "price_original", nullable = false)
	BigDecimal priceOriginal;

	@Column(name = "extra_discount", nullable = false)
	BigDecimal extraDiscount;

	@Column(name = "extra_discount_reason")
	String extraDiscountReason;

	@Column(name = "quantity", nullable = false)
	int quantity;

	@Column(name = "currency")
	String currency;

	@Column(name = "tax_rate")
	BigDecimal taxRate;

	@Column(name = "tax_amount")
	BigDecimal taxAmount;

	@Column(name = "note", length = 500)
	String note;

	@Column(name = "status", nullable = false)
	boolean status;

	@OneToMany(mappedBy = "orderDetail", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	List<OrderHistory> listOrderHistory;

	public OrderDetail(Long id) {
		this.id = id;
	}
}