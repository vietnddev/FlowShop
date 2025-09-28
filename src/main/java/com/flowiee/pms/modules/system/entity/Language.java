package com.flowiee.pms.modules.system.entity;

import java.io.Serial;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
    static final long serialVersionUID = 1L;

    @Column(name = "message_key", nullable = false)
    String messageKey;

    @Column(name = "message_value", nullable = false)
    String messageValue;

    @Column(name = "module", nullable = false)
    String module;

    @Column(name = "page", nullable = false)
    String page;

	@Column(name = "component_type")
    String componentType;
    
    @Column(name = "locale ", nullable = false)
    String locale;
}