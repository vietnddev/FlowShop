package com.flowiee.pms.customer.mapper;

import com.flowiee.pms.customer.dto.CustomerDTO;
import com.flowiee.pms.customer.entity.Customer;
import com.flowiee.pms.customer.model.CustomerSummaryProjection;
import com.flowiee.pms.shared.util.CoreUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomerConvert {
    public static CustomerDTO toDto(CustomerSummaryProjection pInput) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(pInput.getCustomerId());
        dto.setCustomerName(pInput.getCustomerName());
        dto.setPhoneDefault(pInput.getPhoneDefault());
        dto.setEmailDefault(pInput.getEmailDefault());
        dto.setAddressDefault(pInput.getAddressDefault());
        dto.setTotalOrders(CoreUtils.coalesce(pInput.getTotalOrders()));
        dto.setCancelledOrders(CoreUtils.coalesce(pInput.getCancelledOrders()));
        dto.setReturnedOrders(CoreUtils.coalesce(pInput.getReturnedOrders()));
        dto.setTotalSpent(CoreUtils.coalesce(pInput.getTotalSpent()));
        dto.setAverageOrderValue(CoreUtils.coalesce(pInput.getAverageOrderValue()));
        dto.setOutstandingDebt(CoreUtils.coalesce(pInput.getOutstandingDebt()));
        dto.setFirstOrderDate(pInput.getFirstOrderDate());
        dto.setLastOrderDate(pInput.getLastOrderDate());
        dto.setCustomerTier(pInput.getCustomerTier());
        return dto;
    }

    public static CustomerDTO toDto(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setCustomerName(customer.getCustomerName());
        if (customer.getDateOfBirth() != null)
            dto.setDateOfBirth(customer.getDateOfBirth());
        dto.setGender(customer.getGender());
        dto.setMaritalStatus(customer.getMaritalStatus());
        dto.setReferralSource(customer.getReferralSource());
        dto.setIsBlackList(customer.getIsBlackList());
        dto.setBlackListReason(customer.getBlackListReason());
        dto.setBonusPoints(customer.getBonusPoints());
        dto.setHasOutstandingBalance(customer.getHasOutstandingBalance());
        dto.setOutstandingBalanceAmount(CoreUtils.coalesce(customer.getOutstandingBalanceAmount()));
        return dto;
    }

    public static List<CustomerDTO> toDTOs(List<CustomerSummaryProjection> pInput) {
        if (CollectionUtils.isEmpty(pInput)) {
            return new ArrayList<>();
        }
        return pInput.stream()
                .map(CustomerConvert::toDto)
                .toList();
    }
}