package com.flowiee.pms.modules.sales.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flowiee.pms.common.enumeration.PriorityLevel;
import com.flowiee.pms.modules.inventory.dto.TransactionGoodsDTO;
import com.flowiee.pms.modules.sales.entity.Customer;
import com.flowiee.pms.modules.system.dto.CategoryDTO;
import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.modules.sales.entity.CustomerDebt;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.sales.entity.OrderHistory;
import com.flowiee.pms.modules.staff.entity.Account;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.enumeration.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class OrderDTO implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

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
	private Account nhanVienBanHang;
	private Category salesChannel;
	private Category paymentMethod;
	private TransactionGoodsDTO transactionGoodsExport;
	private LocalDateTime cancellationDate;
	private Long cancellationReason;
	private LocalDateTime deliverySuccessTime;
	private LocalDateTime deliveryExpectedTime;
	private CategoryDTO deliveryMethod;
	private String deliveredBy;
	private String deliveredStatus;
	private String deliveryPriority;
	private PriorityLevel priorityLevel;
	private String refundAmount;
	private String refundStatus;
	private String referrerCode;
	private String trackingCode;
	private BigDecimal totalWeight;
	private List<OrderHistory> listOrderHistory;
	private List<FileStorage> listImageQR;
	private List<CustomerDebt> customerDebtList;

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

	private BigDecimal totalAmount;
	private BigDecimal totalAmountDiscount;
	private Integer totalProduct;
	private String qrCode;
	private Long cartId;
	private Long ticketExportId;
	private Boolean accumulateBonusPoints;
	private List<OrderDetailDTO> listOrderDetail;
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

	public static OrderDTO toDto(Order pOrder) {
        Category lvDeliveryMethod = pOrder.getDeliveryMethod();
        Category lvSalesChannel = pOrder.getSalesChannel();
        Category lvPaymentMethod = pOrder.getPaymentMethod();
        Customer lvCustomer = pOrder.getCustomer();
        Account lvCashier = pOrder.getNhanVienBanHang();
        BigDecimal lvAmountDiscount = pOrder.getAmountDiscount();

		OrderDTO dto = new OrderDTO();
		dto.setId(pOrder.getId());
		dto.setCode(pOrder.getCode());
		dto.setOrderTime(pOrder.getOrderTime());
		dto.setReceiverName(pOrder.getReceiverName());
		dto.setReceiverPhone(pOrder.getReceiverPhone());
		dto.setReceiverEmail(pOrder.getReceiverEmail());
		dto.setReceiverAddress(pOrder.getReceiverAddress());
		dto.setOrderStatus(pOrder.getOrderStatus());
		dto.setCreatedAt(pOrder.getCreatedAt());
		dto.setCustomerId(lvCustomer.getId());
		dto.setCustomerName(lvCustomer.getCustomerName());
		dto.setSalesChannelId(lvSalesChannel.getId());
		dto.setSalesChannelName(lvSalesChannel.getName());
		dto.setOrderStatus(dto.getOrderStatus());
		dto.setOrderStatusName(dto.getOrderStatus().getName());
		dto.setPayMethodId(lvPaymentMethod != null ? lvPaymentMethod.getId() : null);
		dto.setPayMethodName(lvPaymentMethod != null ? lvPaymentMethod.getName() : null);
		dto.setDeliveryMethod(lvDeliveryMethod != null ? new CategoryDTO(lvDeliveryMethod.getId(), lvDeliveryMethod.getName()) : new CategoryDTO());
		dto.setCashierId(lvCashier.getId());
		dto.setCashierName(lvCashier.getFullName());
		dto.setShippingCost(pOrder.getShippingCost());
		dto.setCodFee(pOrder.getCodFee());
		dto.setAmountDiscount(lvAmountDiscount != null ? lvAmountDiscount : new BigDecimal(0));
		dto.setCouponCode(pOrder.getCouponCode());
		dto.setPaymentStatus(pOrder.getPaymentStatus() != null && pOrder.getPaymentStatus());
		dto.setPaymentTime(pOrder.getPaymentTime());
		dto.setPaymentAmount(pOrder.getPaymentAmount());
		dto.setPaymentNote(pOrder.getPaymentNote());
		dto.setNote(pOrder.getNote());
		dto.setListOrderDetail(OrderDetailDTO.fromOrderDetails(pOrder.getListOrderDetail()));

		return dto;
	}

	public static List<OrderDTO> fromOrders(List<Order> pOrders) {
		if (CollectionUtils.isEmpty(pOrders)) {
			return new ArrayList<>();
		}
		return pOrders.stream()
				.map(OrderDTO::toDto)
				.toList();
	}
}