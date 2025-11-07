package com.flowiee.pms.modules.system.service;

import com.flowiee.pms.modules.system.dto.ScheduleDTO;

import java.util.List;

public interface ScheduleService {
    List<ScheduleDTO> getSchedules();
}