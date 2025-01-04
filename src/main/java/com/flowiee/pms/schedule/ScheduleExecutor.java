package com.flowiee.pms.schedule;

import com.flowiee.pms.entity.system.Schedule;
import com.flowiee.pms.entity.system.ScheduleStatus;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.repository.system.ScheduleRepository;
import com.flowiee.pms.repository.system.ScheduleStatusRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.common.enumeration.ScheduleTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
public abstract class ScheduleExecutor extends BaseService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ScheduleStatusRepository scheduleStatusRepository;

    private ScheduleStatus mvScheduleStatus;
    private ScheduleTask mvScheduleTask;
    protected boolean enableLog = false;

    public ScheduleExecutor() {
        super();
    }

    public abstract void init() throws AppException;
    public abstract void doProcesses() throws AppException;

    public void init(ScheduleTask pScheduleTask) throws AppException {
        this.mvScheduleTask = pScheduleTask;
        startSchedule();
        try {
            preProcesses(mvScheduleStatus);
            doProcesses();
            postProcesses(mvScheduleStatus);
        } catch (AppException ex) {
            setErrorMsg(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        } finally {
            endSchedule();
        }
    }

    public void startSchedule() {
        ScheduleTask lvScheduleTask = mvScheduleTask;
        if (lvScheduleTask == null) {
            return;
        }
        if (enableLog) {
            logger.info("Schedule task " + lvScheduleTask.name() + " start");
        }
        Optional<Schedule> schedule = scheduleRepository.findById(lvScheduleTask.name());
        if (schedule.isEmpty() || !schedule.get().isEnable()) {
            //logger.warn(String.format("Schedule %s is not defined in the database!", lvScheduleTask.name()));
            //throw new AppException(String.format("Schedule %s is not defined in the database!", lvScheduleTask.name()));
            return;
        }
        mvScheduleStatus = scheduleStatusRepository.save(ScheduleStatus.builder()
                .schedule(schedule.get())
                .startTime(LocalDateTime.now())
                .build());
    }

    public void preProcesses(ScheduleStatus pScheduleStatus) {}

    public void postProcesses(ScheduleStatus pScheduleStatus) {}

    public void endSchedule() {
        ScheduleStatus lvScheduleStatus = mvScheduleStatus;
        if (lvScheduleStatus == null) {
            return;
        }
        lvScheduleStatus.setEndTime(LocalDateTime.now());
        lvScheduleStatus.setDuration(ChronoUnit.SECONDS.between(lvScheduleStatus.getStartTime(), lvScheduleStatus.getEndTime()) + " SECOND");
        lvScheduleStatus.setStatus(lvScheduleStatus.getErrorMsg() == null ? "success" : "fail");
        scheduleStatusRepository.save(lvScheduleStatus);
        if (enableLog) {
            logger.info("Schedule task " + lvScheduleStatus.getSchedule().getScheduleId() + " end");
        }
    }

    protected void setErrorMsg(String pMessage) {
        mvScheduleStatus.setErrorMsg(pMessage);
    }

    public ScheduleTask getScheduleTask() {
        return mvScheduleTask;
    }
}