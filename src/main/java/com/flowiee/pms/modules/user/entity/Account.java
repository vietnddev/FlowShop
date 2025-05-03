package com.flowiee.pms.modules.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;

import com.flowiee.pms.modules.log.entity.SystemLog;
import com.flowiee.pms.modules.sales.entity.Customer;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.common.enumeration.AccountStatus;
import com.flowiee.pms.modules.system.entity.Branch;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.modules.system.entity.ImportHistory;
import com.flowiee.pms.modules.leave.entity.LeaveApplication;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.util.Assert;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Builder
@Entity
@Table(name = "account")
@NamedEntityGraph(
	name = "Account.withBranchAndGroupAccount",
	attributeNodes = {
		@NamedAttributeNode("branch"),
		@NamedAttributeNode("groupAccount")
	}
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account extends BaseEntity implements Serializable {
	@Serial
	static final long serialVersionUID = 1L;

	@Column(name = "username", nullable = false, unique = true)
	String username;

	@JsonIgnore
	@Column(name = "password", nullable = false)
	String password;

	@Column(name = "fullname", nullable = false)
	String fullName;

	@Column(name = "sex", nullable = false)
	boolean sex;

	@Column(name = "phone_number", length = 15, unique = true)
	String phoneNumber;

	@Column(name = "email", length = 50, unique = true)
	String email;

	@Column(name = "address")
	String address;

	@Column(name = "avatar")
	String avatar;

	@Column(name = "remark")
	String remark;

	@Column(name = "role")
	String role;

	@Column(name = "is_fulltime_staff")
	Boolean isPartTimeStaff;

	@Column(name = "remaining_leave_days")
	Integer remainingLeaveDays;

	@Column(name = "line_manager_id")
	Long lineManagerId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_account")
	GroupAccount groupAccount;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branch_id", nullable = false)
	Branch branch;

	@Column(name = "reset_tokens", unique = true)
	String resetTokens;

	@Column(name = "reset_token_date")
	LocalDateTime resetTokenDate;

	@Column(name = "password_expire_date")
	LocalDate passwordExpireDate;

	@Column(name = "fail_logon_count")
	Integer failLogonCount;

	@Column(name = "status")
	String status;

	@JsonIgnore
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	List<FileStorage> listImages;

	@JsonIgnore
	@OneToMany(mappedBy = "nhanVienBanHang", fetch = FetchType.LAZY)
	List<Order> listOrder;

	@JsonIgnore
	@OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
	List<Customer> listCustomer;

	@JsonIgnore
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	List<ImportHistory> listHistoryImportData;

	@JsonIgnore
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	List<SystemLog> listLog;

	@JsonIgnore
	@OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
	List<LeaveApplication> listLeaveApplication;

	@Transient
	String ip;

	public Account(Long id) {
		super.id = id;
	}

	public Account(Long id, String username, String fullName) {
		super.id = id;
		this.username = username;
		this.fullName = fullName;
	}

	public FileStorage getAvatar(Long pImageId) {
		if (getListImages() != null) {
			return getListImages().stream()
					.filter(avatar -> avatar.getId().equals(pImageId))
					.findAny()
					.orElse(null);
		}
		return null;
	}

	public FileStorage getAvatar() {
		if (getListImages() != null) {
			for (FileStorage avatar : getListImages()) {
				if (avatar.isActive())
					return avatar;
			}
			return null;
		}
		return null;
	}

	public boolean isPasswordExpired() {
		return passwordExpireDate != null && passwordExpireDate.isBefore(LocalDate.now());
	}

	public boolean isResetTokenExpired(int pValidityPeriod) {
		Assert.notNull(resetTokenDate, "ResetTokenDate not null!");
		return resetTokenDate.plusMinutes(pValidityPeriod).isBefore(LocalDateTime.now());
	}

	public boolean isNormal() {
		Assert.notNull(status, "Status not null!");
		return Objects.equals(AccountStatus.N.name(), status);
	}

	public boolean isLocked() {
		Assert.notNull(status, "Status not null!");
		return Objects.equals(AccountStatus.L.name(), status);
	}

	public boolean isClosed() {
		Assert.notNull(status, "Status not null!");
		return Objects.equals(AccountStatus.C.name(), status);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Account [username=");
		builder.append(username);
		builder.append(", fullName=");
		builder.append(fullName);
		builder.append(", sex=");
		builder.append(sex);
		builder.append(", phoneNumber=");
		builder.append(phoneNumber);
		builder.append(", email=");
		builder.append(email);
		builder.append(", address=");
		builder.append(address);
		builder.append(", avatar=");
		builder.append(avatar);
		builder.append(", remark=");
		builder.append(remark);
		builder.append(", remark=");
		builder.append(role);
		builder.append(", status=");
		builder.append(status);
		builder.append(", ip=");
		builder.append(ip);
		builder.append("]");
		return builder.toString();
	}
}