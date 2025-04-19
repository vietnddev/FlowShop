package com.flowiee.pms.common.base.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowiee.pms.common.utils.CommonUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditEntity {
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "created_at", updatable = false, columnDefinition = "timestamp default current_timestamp")
    @CreatedDate
    LocalDateTime createdAt;

    @Column(name = "created_by", updatable = false)
    @CreatedBy
    protected Long createdBy;

    @JsonIgnore
    @Column(name = "last_updated_at", columnDefinition = "timestamp default current_timestamp")
    @LastModifiedDate
    LocalDateTime lastUpdatedAt;

    @JsonIgnore
    @Column(name = "last_updated_by")
    @LastModifiedBy
    String lastUpdatedBy;

    @JsonIgnore
    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    @JsonIgnore
    @Column(name = "deleted_by")
    String deletedBy;

    @PrePersist
    public void onCreate() {
        if (createdBy == null) {
            createdBy = CommonUtils.getUserPrincipal().getId();
        }
    }

    @PreUpdate
    public void onUpdate() {
        if (lastUpdatedBy == null) {
            lastUpdatedBy = CommonUtils.getUserPrincipal().getUsername();
        }
    }
}