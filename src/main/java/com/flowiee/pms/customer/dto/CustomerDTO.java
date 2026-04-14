package com.flowiee.pms.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flowiee.pms.shared.base.BaseDTO;
import com.flowiee.pms.system.dto.CategoryDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDTO extends BaseDTO implements Serializable {
    String code;
    String customerName;

    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dateOfBirth;

    String gender;
    String maritalStatus;
    String referralSource;
    Boolean isBlackList;
    String blackListReason;
    Integer bonusPoints = 0;
    Boolean hasOutstandingBalance = false;
    BigDecimal outstandingBalanceAmount = BigDecimal.ZERO;
    Boolean isVIP = false;
    CategoryDTO groupCustomer;

	String phoneDefault;
    String emailDefault;
    String addressDefault;
    BigDecimal averageOrderValue;
    String customerGroup;
    String profilePictureUrl;
    LocalDateTime firstOrderDate;
    LocalDateTime lastOrderDate;
    BigDecimal totalSpent;
    BigDecimal outstandingDebt;
    Integer totalOrders;
    Integer cancelledOrders;
    Integer returnedOrders;
    String customerTier;

    List<CustomerContactDTO> listCustomerContact;
}