package com.flowiee.pms.modules.system.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "serial")
public class Serial {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "prefix", nullable = false)
    private String prefix;

    @Column(name = "init_value", nullable = false)
    private Integer initValue;

    @Column(name = "current_value", nullable = false)
    private Integer currentValue;
}