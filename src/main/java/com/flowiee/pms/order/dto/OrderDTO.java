package com.flowiee.pms.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flowiee.pms.shared.enums.PriorityLevel;
import com.flowiee.pms.customer.dto.CustomerDTO;
import com.flowiee.pms.order.enums.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class OrderDTO implements Serializable {
	private Long id;
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime createdAt;
	private String code;
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime orderTime;
	private String receiverAddress;
	private String receiverName;
	private String receiverPhone;
	private String receiverEmail;
	private String couponCode;
	private BigDecimal shippingCost;
	private BigDecimal codFee;
	private BigDecimal amountDiscount;
	private Boolean paymentStatus;
	private LocalDateTime paymentTime;
	private BigDecimal paymentAmount;
	private String paymentNote;
	private String note;

	private CustomerDTO customer;
	private String customerType;
	private String confirmedBy;
	private LocalDateTime confirmedTime;
	private String customerNote;
	private Boolean isGiftWrapped;
	private BigDecimal giftWrapCost;
	private BigDecimal packagingCost;
	private LocalDateTime cancellationDate;
	private Long cancellationReason;
	private LocalDateTime deliverySuccessTime;
	private LocalDateTime deliveryExpectedTime;
	private String deliveredBy;
	private String deliveredStatus;
	private String deliveryPriority;
	private PriorityLevel priorityLevel;
	private String refundAmount;
	private String refundStatus;
	private String referrerCode;
	private String trackingCode;
	private BigDecimal totalWeight;

	private Long customerId;
	private String customerName;
	private Long salesChannelId;
	private String salesChannelName;
	private OrderStatus orderStatus;
	private Long orderStatusId;
	private String orderStatusName;
	private Long payMethodId;
	private String payMethodName;
	private Long cashierId;
	private String cashierName;
	private Long deliveryMethodId;
	private String deliveryMethodName;

	private BigDecimal totalAmount;
	private BigDecimal totalAmountDiscount;
	private Integer totalProduct;
	private String qrCode;
	private Long cartId;
	private Long ticketExportId;
	private Boolean accumulateBonusPoints;
	private List<OrderDetailDTO> items;

	public OrderDTO(Long id, String code, LocalDateTime orderTime, String receiptName, String receiptPhone, String receiptEmail, String receiptAddress, OrderStatus orderStatus) {
		this.id = id;
		setCode(code);
		setOrderTime(orderTime);
		setReceiverName(receiptName);
		setReceiverPhone(receiptPhone);
		setReceiverEmail(receiptEmail);
		setReceiverAddress(receiptAddress);
		setOrderStatus(orderStatus);
	}
}