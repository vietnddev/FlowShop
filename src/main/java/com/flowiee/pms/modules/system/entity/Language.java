package com.flowiee.pms.modules.system.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.flowiee.pms.common.base.entity.BaseEntity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "languages")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Language extends BaseEntity implements Serializable {
    @Serial
	static final long serialVersionUID = 1L;

    @Column(name = "module")
    String module;

    @Column(name = "screen")
    String screen;

	@Column(name = "code", nullable = false)
    String code;
    
    @Column(name = "property_key", nullable = false)
    String key;
    
    @Column(name = "value", nullable = false)
    String value;
}