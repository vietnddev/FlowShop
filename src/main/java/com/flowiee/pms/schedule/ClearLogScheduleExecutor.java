package com.flowiee.pms.schedule;

import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.entity.system.EventLog;
import com.flowiee.pms.entity.system.SystemConfig;
import com.flowiee.pms.entity.system.SystemLog;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.repository.system.ConfigRepository;
import com.flowiee.pms.repository.system.EventLogRepository;
import com.flowiee.pms.repository.system.SystemLogRepository;
import com.flowiee.pms.common.enumeration.ConfigCode;
import com.flowiee.pms.common.enumeration.ScheduleTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ClearLogScheduleExecutor extends ScheduleExecutor {
    @Autowired
    private SystemLogRepository systemLogRepository;
    @Autowired
    private EventLogRepository eventLogRepository;
    @Autowired
    private ConfigRepository configRepository;

    public ClearLogScheduleExecutor() {
        super();
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * ?")
    @Override
    public void init() throws AppException {
        enableLog = true;
        super.init(ScheduleTask.ClearLog);
    }

    @Override
    public void doProcesses() throws AppException {
        if (!isEnableDeleteLog()) {
            return;
        }

        SystemConfig lvDayDeleteSystemLogConfig = configRepository.findByCode(ConfigCode.dayDeleteSystemLog.name());
        if (SysConfigUtils.isValid(lvDayDeleteSystemLogConfig)) {
            return;
        }

        int lvDayDeleteSystemLog = Integer.parseInt(lvDayDeleteSystemLogConfig.getValue());
        LocalDateTime lvFromCreatedTime =
                LocalDateTime.now().minusDays(lvDayDeleteSystemLog).withNano(0).withSecond(0).withMinute(0).withHour(0);

        List<EventLog> eventLogList = eventLogRepository.getEventLogFrom(lvFromCreatedTime);
        for (EventLog eventLog : eventLogList) {
            eventLogRepository.deleteById(eventLog.getRequestId());
        }
        logger.info("ClearLogScheduleExecutor has completed clearing the event log");

        List<SystemLog> systemLogList = systemLogRepository.getSystemLogFrom(lvFromCreatedTime);
        for (SystemLog systemLog : systemLogList) {
            systemLogRepository.deleteById(systemLog.getId());
        }
        logger.info("ClearLogScheduleExecutor has completed clearing the system log");

        //clear more logs here
    }

    private boolean isEnableDeleteLog() {
        if (!SysConfigUtils.isYesOption(ConfigCode.deleteSystemLog)) {
            return false;
        }
        return true;
    }
}