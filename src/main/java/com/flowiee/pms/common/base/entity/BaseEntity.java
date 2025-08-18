package com.flowiee.pms.common.base.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity extends AuditEntity implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}