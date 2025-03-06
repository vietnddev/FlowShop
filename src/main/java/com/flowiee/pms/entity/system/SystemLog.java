package com.flowiee.pms.entity.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.BaseEntity;
import com.flowiee.pms.common.utils.CommonUtils;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;

@Builder
@Entity
@Table(name = "action_log")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SystemLog extends BaseEntity implements java.io.Serializable {
	@Serial
	static final long serialVersionUID = 1L;
	public static String EMPTY = "-";

	@Column(name = "module", length = 50, nullable = false)
	String module;

	@Column(name = "function", nullable = false)
	String function;

	@Column(name = "title")
	String title;

	@Column(name = "object")
	String object;

	@Column(name = "action_mode", nullable = false)
	String mode;

	@Column(name = "content", length = 4000, nullable = false)
	String content;

	@Column(name = "content_change", length = 4000)
	String contentChange;

	@Column(name = "ip", length = 20)
	String ip;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", nullable = false)
	Account account;

	@Transient
	String username;

	@Transient
	String accountName;

	@PreUpdate
	public void updateAudit() {
		if (ip == null) {
			ip = CommonUtils.getUserPrincipal().getIp();
		} else {
			ip = "unknown";
		}
	}

	@Override
	public String toString() {
		return "SystemLog [id=" + super.id + ", module=" + module + ", action=" + function + ", content=" + content + ", contentChange=" + contentChange + ", ip=" + ip + ", username=" + username + "]";
	}
}