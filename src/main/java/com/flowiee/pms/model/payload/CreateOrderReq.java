package com.flowiee.pms.model.payload;

import com.flowiee.pms.model.dto.OrderDTO;
import com.flowiee.pms.common.enumeration.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class CreateOrderReq {
    private Long cartId;
    private Long customerId;
    private Long salesAssistantId;
    private Long salesChannelId;
    private Long paymentMethodId;
    private String orderStatus;
    private String note;
    private String orderTime;
    private String voucherUsed;
    private String recipientName;
    private String recipientPhone;
    private String recipientEmail;
    private String shippingAddress;
    private BigDecimal amountDiscount;
    private BigDecimal packagingCost;
    private BigDecimal shippingCost;
    private BigDecimal giftWrapCost;
    private BigDecimal codFee;
    private Boolean accumulateBonusPoints;

    public OrderDTO toDTO() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");

        OrderDTO dto = new OrderDTO();
        dto.setCartId(cartId);
        dto.setCustomerId(customerId);
        dto.setCashierId(salesAssistantId);
        dto.setSalesChannelId(salesChannelId);
        dto.setPayMethodId(paymentMethodId);
        dto.setOrderStatus(OrderStatus.valueOf(orderStatus));
        dto.setNote(note);
        dto.setOrderTime(LocalDateTime.parse(orderTime, formatter));
        dto.setVoucherUsedCode(voucherUsed);
        dto.setReceiverName(recipientName);
        dto.setReceiverPhone(recipientPhone);
        dto.setReceiverEmail(recipientEmail);
        dto.setReceiverAddress(shippingAddress);
        dto.setAmountDiscount(amountDiscount);
        
        return dto;
    }
}