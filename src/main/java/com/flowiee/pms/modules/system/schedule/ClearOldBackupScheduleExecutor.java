package com.flowiee.pms.modules.system.schedule;

import com.flowiee.pms.common.base.FlwSys;
import com.flowiee.pms.common.enumeration.ConfigCode;
import com.flowiee.pms.common.enumeration.ScheduleTask;
import com.flowiee.pms.common.enumeration.SystemDir;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.modules.system.entity.SystemConfig;
import com.flowiee.pms.modules.system.repository.ConfigRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Log4j2
@Component
public class ClearOldBackupScheduleExecutor extends ScheduleExecutor {
    @Autowired
    private ConfigRepository configRepository;

    public ClearOldBackupScheduleExecutor() {
        super();
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * ?")
    @Override
    public void init() throws AppException {
        super.init(ScheduleTask.ClearBackup);
    }

    @Override
    public void doProcesses() throws AppException {
        if (!SysConfigUtils.isYesOption(ConfigCode.deleteBackupFile)) {
            return;
        }

        SystemConfig lvDayDeleteBackupFileConfig = FlwSys.getSystemConfigs().get(ConfigCode.dayDeleteBackupFile);
        if (SysConfigUtils.isValid(lvDayDeleteBackupFileConfig)) {
            return;
        }

        int lvDayDeleteBackupFile = lvDayDeleteBackupFileConfig.getIntValue();

        try {
            Path BACKUP_DIR = Paths.get(FileUtils.getSystemDir(SystemDir.BACKUP));
            Files.createDirectories(BACKUP_DIR);

            if (!Files.exists(BACKUP_DIR)) return;

            Files.list(BACKUP_DIR)
                    .filter(p -> p.toString().endsWith(".zip"))
                    .filter(p -> {
                        try {
                            Instant lastModified = Files.getLastModifiedTime(p).toInstant();
                            return lastModified.isBefore(Instant.now().minus(lvDayDeleteBackupFile, ChronoUnit.DAYS));
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                            log.info("Deleted old backup: {}", p);
                        } catch (IOException e) {
                            log.warn("Failed to delete backup: {}", p);
                        }
                    });

        } catch (IOException e) {
            log.error("Error cleaning old backups", e);
        }

        log.info("ClearOldBackupScheduleExecutor has completed");

    }
}