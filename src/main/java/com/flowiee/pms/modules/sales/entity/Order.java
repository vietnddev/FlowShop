package com.flowiee.pms.modules.sales.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.category.entity.Category;
import com.flowiee.pms.modules.inventory.entity.TicketExport;
import com.flowiee.pms.modules.user.entity.Account;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.enumeration.OrderStatus;
import com.flowiee.pms.common.enumeration.PriorityLevel;
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
@Table(name = "orders",
       indexes = {@Index(name = "idx_Order_code", columnList = "code"),
                  @Index(name = "idx_Order_receiverPhone", columnList = "receiver_phone"),
                  @Index(name = "idx_Order_salesAssistantId", columnList = "sales"),
                  @Index(name = "idx_Order_channelId", columnList = "channel"),
                  @Index(name = "idx_Order_customerId", columnList = "customer_id"),
				  @Index(name = "idx_Order_trackingCode", columnList = "tracking_code")})
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

	@Column(name = "customer_type")
	String customerType;

	@Column(name = "confirmed_by")
	String confirmedBy;

	@Column(name = "confirmed_time")
	LocalDateTime confirmedTime;

	@Column(name = "internal_note", length = 500)
	String note;

	@Column(name = "customer_note", length = 500)
	String customerNote;

	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	@Column(name = "order_time", nullable = false)
	LocalDateTime orderTime;

	@Column(name = "coupon_code")
	String couponCode;

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
	Category salesChannel;

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
	BigDecimal paymentAmount;

	@Column(name = "cod_fee")
	BigDecimal codFee;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_export_id")
	TicketExport ticketExport;

//	@JsonIgnore
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "status", nullable = false)
//	Category trangThaiDonHang;

	@Column(name = "cancellation_date")
	LocalDateTime cancellationDate;

	@Column(name = "cancellation_reason")
	Long cancellationReason;

	@Column(name = "successful_delivery_time")
	LocalDateTime deliverySuccessTime;

	@Column(name = "delivery_expected_time")
	LocalDateTime deliveryExpectedTime;

	@Column(name = "delivery_method")
	String deliveryMethod;

	@Column(name = "delivered_by")
	String deliveredBy;

	@Column(name = "delivery_status")
	String deliveredStatus;

	@Column(name = "delivery_priority")
	String deliveryPriority;

	@Enumerated(EnumType.STRING)
	@Column(name = "priority_level")
	PriorityLevel priorityLevel;

	@Column(name = "refund_amount")
	String refundAmount;

	@Column(name = "refund_status")
	String refundStatus;

	@Column(name = "referrer_code")
	String referrerCode;

	@Column(name = "tracking_code")
	String trackingCode;

	@Column(name = "total_weight")
	BigDecimal totalWeight;

	@Enumerated(EnumType.STRING)
	@Column(name = "order_status", length = 10)
	OrderStatus orderStatus;

	@JsonIgnore
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
	List<OrderDetail> listOrderDetail;

	@JsonIgnore
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
	List<OrderHistory> listOrderHistory;

	@JsonIgnore
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	List<FileStorage> listImageQR;

	@JsonIgnore
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	List<CustomerDebt> customerDebtList;

	public Order(long id) {
		super.id = id;
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
				+ ", couponCode=" + couponCode + ", amountDiscount=" + amountDiscount
				+ ", nhanVienBanHang=" + nhanVienBanHang
				+ ", salesChannel=" + salesChannel + ", orderStatus=" + orderStatus + "]";
	}
}