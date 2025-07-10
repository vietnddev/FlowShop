package com.flowiee.pms.modules.sales.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.modules.sales.entity.*;
import com.flowiee.pms.modules.system.dto.CategoryDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDTO extends BaseDTO implements Serializable {
	@Serial
	static final long serialVersionUID = 1L;

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
    BigDecimal orderAvgValue;
    String customerGroup;
    String profilePictureUrl;
    LocalDate lastOrder;
    BigDecimal totalPurchasedAmount;
    Integer totalPurchasedCount;

    //List<OrderDTO> listOrder;
    List<CustomerContactDTO> listCustomerContact;

    public static CustomerDTO fromCustomer(Customer customer) {
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
        //dto.setListOrder(customer.getListOrder());
        //dto.setListProductReviews(customer.getListProductReviews());
        //dto.setListCustomerContact(customer.getListCustomerContact());
        //dto.setLoyaltyTransactionList(customer.getLoyaltyTransactionList());
        dto.setTotalPurchasedAmount(BigDecimal.ZERO);
        //mappingBaseAudit(dto, customer);

        return dto;
    }

    public static List<CustomerDTO> fromCustomers(List<Customer> customers) {
        List<CustomerDTO> listCustomerDTO = new ArrayList<>();
        customers.forEach(customer -> {
            listCustomerDTO.add(fromCustomer(customer));
        });
        return listCustomerDTO;
    }
}