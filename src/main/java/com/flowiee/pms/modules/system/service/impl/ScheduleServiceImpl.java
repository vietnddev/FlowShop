package com.flowiee.pms.modules.system.service.impl;

import com.flowiee.pms.modules.system.dto.ScheduleDTO;
import com.flowiee.pms.modules.system.repository.ScheduleRepository;
import com.flowiee.pms.modules.system.repository.ScheduleStatusRepository;
import com.flowiee.pms.modules.system.schedule.entity.Schedule;
import com.flowiee.pms.modules.system.schedule.entity.ScheduleStatus;
import com.flowiee.pms.modules.system.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleStatusRepository scheduleStatusRepository;

    @Override
    public List<ScheduleDTO> getSchedules() {
        List<ScheduleDTO> lvScheduleDTOList = new ArrayList<>();

        List<Schedule> lvSchedules = scheduleRepository.findAll();
        for (Schedule lvSchedule : lvSchedules) {
            ScheduleStatus lvScheduleLastRun = scheduleStatusRepository.findLatestByScheduleId(lvSchedule.getScheduleId());
            LocalDateTime lvLastRun = lvScheduleLastRun != null ? lvScheduleLastRun.getStartTime() : null;

            lvScheduleDTOList.add(ScheduleDTO.builder()
                    .taskId(lvSchedule.getScheduleId())
                    .taskName(lvSchedule.getScheduleName())
                    .executionTime("N/A")
                    .lastRun(lvLastRun)
                    .note("N/A")
                    .status(lvSchedule.isEnable() ? "Enable" : "Disable")
                    .build());
        }

        return lvScheduleDTOList;
    }
}