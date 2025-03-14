package com.flowiee.pms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.entity.sales.OrderDetail;
import com.flowiee.pms.entity.system.FileStorage;
import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.common.utils.OrderUtils;
import com.flowiee.pms.common.enumeration.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

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
	private List<OrderDetailDTO> listOrderDetailDTO;
	@JsonIgnore
	private List<OrderDetail> listOrderDetail;

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

	public static OrderDTO fromOrder(Order order) {
		OrderDTO dto = new OrderDTO(order.getId(), order.getCode(), order.getOrderTime(), order.getReceiverName(),
				order.getReceiverPhone(), order.getReceiverEmail(), order.getReceiverAddress(), order.getOrderStatus());
		dto.setCreatedAt(order.getCreatedAt());

		dto.setCustomerId(order.getCustomer().getId());
		dto.setCustomerName(order.getCustomer().getCustomerName());

		dto.setSalesChannelId(order.getSalesChannel().getId());
		dto.setSalesChannelName(order.getSalesChannel().getName());

		//dto.setOrderStatusId(dto.getPaymentMethod() != null ? order.getTrangThaiDonHang().getId() : null);
		//dto.setOrderStatusName(dto.getPaymentMethod() != null ? order.getTrangThaiDonHang().getName() : null);
		dto.setOrderStatus(dto.getOrderStatus());
		dto.setOrderStatusName(dto.getOrderStatus().getName());

		dto.setPayMethodId(order.getPaymentMethod() != null ? order.getPaymentMethod().getId() : null);
		dto.setPayMethodName(order.getPaymentMethod() != null ? order.getPaymentMethod().getName() : null);

		dto.setCashierId(order.getNhanVienBanHang().getId());
		dto.setCashierName(order.getNhanVienBanHang().getFullName());

		dto.setTicketExportId(order.getTicketExport() != null ? order.getTicketExport().getId() : null);

		if (ObjectUtils.isNotEmpty(order.getListImageQR())) {
			FileStorage imageQRCode = order.getListImageQR().get(0);//
			dto.setQrCode(FileUtils.getImageUrl(imageQRCode, false));
		}
		dto.setShippingCost(order.getShippingCost());
		dto.setCodFee(order.getCodFee());
		dto.setAmountDiscount(order.getAmountDiscount() != null ? order.getAmountDiscount() : new BigDecimal(0));
		dto.setTotalAmount(OrderUtils.calTotalAmount(order.getListOrderDetail(), BigDecimal.ZERO));
		dto.setTotalAmountDiscount(OrderUtils.calTotalAmount(order.getListOrderDetail(), order.getAmountDiscount()));
		dto.setTotalProduct(OrderUtils.countItemsEachOrder(order.getListOrderDetail()));
		dto.setCouponCode(order.getCouponCode());
		dto.setPaymentStatus(order.getPaymentStatus() != null && order.getPaymentStatus());
		dto.setPaymentTime(order.getPaymentTime());
		dto.setPaymentAmount(order.getPaymentAmount());
		dto.setPaymentNote(order.getPaymentNote());
		dto.setNote(order.getNote());

		dto.setListOrderDetailDTO(OrderDetailDTO.fromOrderDetails(order.getListOrderDetail()));

		return dto;
	}

	public static List<OrderDTO> fromOrders(List<Order> orders) {
		List<OrderDTO> list = new ArrayList<>();
		if (ObjectUtils.isNotEmpty(orders)) {
			for (Order p : orders) {
				list.add(OrderDTO.fromOrder(p));
			}
		}
		return list;
	}
}