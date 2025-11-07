package com.flowiee.pms.modules.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ScheduleDTO {
    private String taskId;
    private String taskName;
    private String executionTime;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime lastRun;
    private String note;
    private String status;
}