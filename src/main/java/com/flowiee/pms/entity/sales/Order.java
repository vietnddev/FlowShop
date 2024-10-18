package com.flowiee.pms.entity.sales;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.entity.BaseEntity;
import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.entity.system.Account;
import com.flowiee.pms.entity.system.FileStorage;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Builder
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order extends BaseEntity implements Serializable {
	@Serial
	static final long serialVersionUID = 1L;

	@Column(name = "code", length = 20, nullable = false, unique = true)
	String code;

	@Column(name = "receiver_name")
	String receiverName;

	@Column(name = "receiver_phone")
	String receiverPhone;

	@Column(name = "receiver_email")
	String receiverEmail;

	@Column(name = "receiver_address")
	String receiverAddress;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	Customer customer;

	@Column(name = "note", length = 500)
	String note;

	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Column(name = "order_time", nullable = false)
	LocalDateTime orderTime;

	@Column(name = "voucher_used")
	String voucherUsedCode;

	@Column(name = "amount_discount", nullable = false)
	BigDecimal amountDiscount;

	@Column(name = "shipping_cost")
	BigDecimal shippingCost;

	@Column(name = "is_gift_wrapped")
	Boolean isGiftWrapped;

	@Column(name = "gift_wrap_cost")
	BigDecimal giftWrapCost;

	@Column(name = "packaging_cost")
	BigDecimal packagingCost;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sales")
	Account nhanVienBanHang;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "channel", nullable = false)
	Category kenhBanHang;

	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Column(name = "payment_time")
	LocalDateTime paymentTime;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_method")
	Category paymentMethod;

	@Column(name = "payment_status")
	Boolean paymentStatus;

	@Column(name = "payment_note")
	String paymentNote;

	@Column(name = "payment_amount")
	Float paymentAmount;

	@Column(name = "cod_fee")
	BigDecimal codFee;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_export_id")
	TicketExport ticketExport;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status", nullable = false)
	Category trangThaiDonHang;

	@Column(name = "cancellation_date")
	LocalDateTime cancellationDate;

	@Column(name = "cancellation_reason")
	String cancellationReason;

	@JsonIgnore
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
	List<OrderDetail> listOrderDetail;

	@JsonIgnore
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
	List<OrderHistory> listOrderHistory;

	@JsonIgnore
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	List<FileStorage> listImageQR;

	public Order(long id) {
		super.id = id;
	}

	public static BigDecimal calTotalAmount(List<OrderDetail> orderItems) {
		BigDecimal totalAmount = BigDecimal.ZERO;
		if (orderItems != null) {
			for (OrderDetail d : orderItems) {
				totalAmount = totalAmount.add((d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity()))).subtract(d.getExtraDiscount()));
			}
		}
		return totalAmount;
	}

	public static BigDecimal calTotalAmountDiscount(BigDecimal totalAmount, BigDecimal amountDiscount) {
		return totalAmount.subtract(amountDiscount);
	}

	public static int calTotalProduct(List<OrderDetail> orderItems) {
		int totalItems = 0;
		if (orderItems != null) {
			for (OrderDetail d : orderItems) {
				totalItems += d.getQuantity();
			}
		}
		return totalItems;
	}

	public FileStorage getQRCode() {
		if (getListImageQR() != null) {
			for (FileStorage codeQR : getListImageQR()) {
				if (codeQR.isActive())
					return codeQR;
			}
			return null;
		}
		return null;
	}

	@Override
	public String toString() {
		return "Order [id=" + super.id + ", maDonHang=" + code + ", receiverName=" + receiverName + ", receiverPhone=" + receiverPhone
				+ ", receiverEmail=" + receiverEmail + ", receiverAddress=" + receiverAddress + ", customer=" + customer
				+ ", ghiChu=" + note + ", orderTime=" + orderTime
				+ ", voucherUsedCode=" + voucherUsedCode + ", amountDiscount=" + amountDiscount
				+ ", nhanVienBanHang=" + nhanVienBanHang
				+ ", kenhBanHang=" + kenhBanHang + ", trangThaiDonHang=" + trangThaiDonHang + "]";
	}
}