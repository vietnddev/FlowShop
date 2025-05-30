package com.flowiee.pms.modules.sales.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.modules.inventory.entity.ProductReview;
import com.flowiee.pms.modules.inventory.entity.GiftRedemption;
import com.flowiee.pms.modules.sales.dto.CustomerDTO;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@Entity
@Table(name = "customer")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer extends BaseEntity implements Serializable {
	@Serial
	static final long serialVersionUID = 1L;

	@Column(name = "customer_code", length = 20)
	String code;

	@Column(name = "customer_name", length = 100, nullable = false)
	String customerName;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@Column(name = "birthday")
	LocalDate dateOfBirth;

	@Column(name = "gender", nullable = false, length = 10)
	String gender;

	@Column(name = "marital_status")
	String maritalStatus;

	@Column(name = "referral_source")
	String referralSource;

	@Column(name = "black_list")
	Boolean isBlackList;

	@Column(name = "black_list_reason")
	String blackListReason;

	@Column(name = "bonus_points")
	Integer bonusPoints = 0;

	@Column(name = "has_outstanding_balance")
	Boolean hasOutstandingBalance = false;

	@Column(name = "outstanding_balance_amount")
	BigDecimal outstandingBalanceAmount = BigDecimal.ZERO;

	@Column(name = "is_vip")
	Boolean isVIP = false;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "group_customer_id")
	Category groupCustomer;

	@JsonIgnore
	@JsonIgnoreProperties("customer")
	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
	List<Order> listOrder;

	@JsonIgnore
	@JsonIgnoreProperties("customer")
	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
	List<ProductReview> listProductReviews;

	@JsonIgnore
	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	List<CustomerContact> listCustomerContact;

	@JsonIgnore
	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
	List<LoyaltyTransaction> loyaltyTransactionList;

	@JsonIgnore
	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
	List<GiftRedemption> giftRedemptionList;

	@JsonIgnore
	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	List<CustomerDebt> customerDebtList;

	public Customer(long id) {
		super.id = id;
	}

	public Customer(long id, String customerName) {
		super.id = id;
		this.customerName = customerName;
	}

	public static Customer fromCustomerDTO(CustomerDTO dto) {
		Customer customer = Customer.builder()
			.customerName(dto.getCustomerName())
			.dateOfBirth(dto.getDateOfBirth())
			.gender(dto.getGender())
			.build();
		customer.setId(dto.getId());
		return customer;
	}

	public boolean isWalkInCustomer() {
		return "WIC".equals(CoreUtils.trim(code));
	}

	@Override
	public String toString() {
		return "Customer [id=" + super.id + ", customerName=" + customerName + ", birthday=" + dateOfBirth + ", sex=" + gender + "]";
	}
}