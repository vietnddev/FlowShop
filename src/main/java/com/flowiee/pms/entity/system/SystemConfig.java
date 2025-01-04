package com.flowiee.pms.entity.system;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.entity.BaseEntity;

import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.enumeration.ConfigCode;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table (name = "config")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SystemConfig extends BaseEntity implements Serializable {
	@Serial
	static final long serialVersionUID = 1L;

	@Column(name = "code", nullable = false, unique = true)
	String code;

	@Column(name = "name", nullable = false)
	String name;

	@Column(name = "value", length = 1000)
	String value;

	@Column(name = "sort")
	Integer sort;

	public SystemConfig(String code, String name, String value) {
		this.code = code;
		this.name = name;
		this.value = value;
	}

	public SystemConfig(ConfigCode code, String name, String value) {
		this(code.name(), name, value);
	}

	public boolean isYesOption() {
		return value != null && "Y".equals(value.trim());
	}

	public Integer getIntValue() {
		if (CoreUtils.isNumeric(value)) {
			return Integer.parseInt(value);
		}
		return null;
	}

	@Override
	public String toString() {
		return "SystemConfig [id=" + super.id + ", code=" + code + ", name=" + name + ", value=" + value + ", sort=" + sort + "]";
	}
}