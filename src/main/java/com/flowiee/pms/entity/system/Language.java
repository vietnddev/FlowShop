package com.flowiee.pms.entity.system;

import java.io.Serial;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.flowiee.pms.base.entity.BaseEntity;

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
    
    @Column(name = "key", nullable = false)
    String key;
    
    @Column(name = "value", nullable = false)
    String value;
}