package com.flowiee.pms.entity.system;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Builder
@Entity
@Table (name = "role")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountRole extends BaseEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;
	
	@Column(name = "module", nullable = false)
	@NotNull
	String module;

	@Column(name = "action", nullable = false)
	@NotNull
	String action;

	@Column(name = "account_id")
	Long accountId;

	@Column(name = "group_id")
	Long groupId;

	@Override
	public String toString() {
		return "AccountRole [module=" + module + ", action=" + action + ", accountId=" + accountId + "]";
	}
}