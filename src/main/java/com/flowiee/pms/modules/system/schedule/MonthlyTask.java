package com.flowiee.pms.modules.system.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MonthlyTask {
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void executeTask() {

    }
}