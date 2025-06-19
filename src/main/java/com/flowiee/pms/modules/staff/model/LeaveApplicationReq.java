package com.flowiee.pms.modules.staff.model;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LeaveApplicationReq {
    private Long employee;
    private Long leaveType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String reason;
    private Long[] replacementEmployees;
    private Integer totalLeaveDays;
    private String managerComment;
}