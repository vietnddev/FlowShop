package com.flowiee.pms.common.base.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

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

    public static void mappingBaseAudit(BaseEntity sourceEntity, BaseEntity toEntity) {
        sourceEntity.setId(toEntity.getId());
        sourceEntity.setCreatedAt(toEntity.getCreatedAt());
        sourceEntity.setCreatedBy(toEntity.getCreatedBy());
        sourceEntity.setLastUpdatedAt(toEntity.getLastUpdatedAt());
        sourceEntity.setLastUpdatedBy(toEntity.getLastUpdatedBy());
        sourceEntity.setDeletedAt(toEntity.getDeletedAt());
        sourceEntity.setDeletedBy(toEntity.getDeletedBy());
    }
}