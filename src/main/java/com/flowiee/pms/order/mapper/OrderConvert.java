package com.flowiee.pms.order.mapper;

import com.flowiee.pms.customer.entity.Customer;
import com.flowiee.pms.order.dto.OrderDTO;
import com.flowiee.pms.order.dto.OrderDetailDTO;
import com.flowiee.pms.order.entity.Order;
import com.flowiee.pms.system.entity.Account;
import com.flowiee.pms.system.entity.Category;

import java.math.BigDecimal;

public class OrderConvert {
    public static OrderDTO toDto(Order pOrder) {
        Category lvDeliveryMethod = pOrder.getDeliveryMethod();
        Category lvSalesChannel = pOrder.getSalesChannel();
        Category lvPaymentMethod = pOrder.getPaymentMethod();
        Customer lvCustomer = pOrder.getCustomer();
        Account lvCashier = pOrder.getSeller();
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
        dto.setOrderStatusName(dto.getOrderStatus().getLabel());
        dto.setPayMethodId(lvPaymentMethod != null ? lvPaymentMethod.getId() : null);
        dto.setPayMethodName(lvPaymentMethod != null ? lvPaymentMethod.getName() : null);
        dto.setDeliveryMethodId(lvDeliveryMethod != null ? lvDeliveryMethod.getId() : null);
        dto.setDeliveryMethodName(lvDeliveryMethod != null ? lvDeliveryMethod.getName() : null);
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
        dto.setItems(OrderDetailDTO.toDTOs(pOrder.getListOrderDetail()));

        return dto;
    }
}