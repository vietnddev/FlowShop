package com.flowiee.pms.modules.system.service.impl;

import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.common.enumeration.MasterObject;
import com.flowiee.pms.common.enumeration.SystemDir;
import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.modules.system.service.FileBackupService;
import com.flowiee.pms.modules.system.service.SystemLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class FileBackupServiceImpl implements FileBackupService {
    private final SystemLogService systemLogService;

    @Override
    public File createBackupZip() throws IOException {
        String lvSourceBackupDirPath = FileUtils.getSystemDir(SystemDir.UPLOAD);
        Path sourceDir = Paths.get(lvSourceBackupDirPath);
        if (!Files.exists(sourceDir)) {
            throw new IOException("Source directory not found: " + lvSourceBackupDirPath);
        }

        Path BACKUP_DIR = Paths.get(FileUtils.getSystemDir(SystemDir.BACKUP));
        Files.createDirectories(BACKUP_DIR);

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Path zipPath = BACKUP_DIR.resolve("backup_" + timestamp + ".zip");

        FileUtils.zipDirectory(sourceDir, zipPath);
        log.info("Created backup: {}", zipPath);

        File backedUpFile = zipPath.toFile();

        systemLogService.writeLogCreate(MODULE.SYSTEM, ACTION.SYS_DATA_BACKUP, MasterObject.FileStorage, "Backup data", backedUpFile.getName());

        return backedUpFile;
    }
}