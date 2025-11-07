package com.flowiee.pms.modules.system.schedule.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Entity
@Table(name = "schedule_status",
       indexes = {@Index(name = "idx_ScheduleStatus_scheduleId", columnList = "schedule_id")})
@NoArgsConstructor
@Getter
@Setter
public class ScheduleStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Integer id;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    Schedule schedule;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration")
    private String duration;

    @Column(name = "status")
    private String status;

    @Column(name = "error_msg")
    private String errorMsg;
}