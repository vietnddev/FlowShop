package com.flowiee.pms.modules.staff.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.common.enumeration.LeaveStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "leave_application")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveApplication extends BaseEntity implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    Account employee;

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    Category leaveType;

    @Column(name = "start_date", nullable = false)
    LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    LocalDateTime endDate;

    @Column(name = "reason", nullable = false)
    String reason;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    LeaveStatus status;

    @Column(name = "replacement_employee")
    String replacementEmployee; // Người thay thế (nếu có)

    @Column(name = "request_date", nullable = false)
    LocalDateTime requestDate;

    @Column(name = "response_date")
    LocalDateTime responseDate;

    @Column(name = "manager_comment")
    String managerComment;

    @JsonIgnore
    @Transient
    Integer totalLeaveDays;
}