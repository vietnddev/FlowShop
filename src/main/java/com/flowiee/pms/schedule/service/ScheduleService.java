package com.flowiee.pms.schedule.service;

import com.flowiee.pms.schedule.dto.ScheduleDTO;

import java.util.List;

public interface ScheduleService {
    List<ScheduleDTO> getSchedules();
}