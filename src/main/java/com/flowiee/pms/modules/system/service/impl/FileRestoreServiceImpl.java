package com.flowiee.pms.modules.system.service.impl;

import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.common.enumeration.MasterObject;
import com.flowiee.pms.common.enumeration.SystemDir;
import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.modules.system.service.FileRestoreService;
import com.flowiee.pms.modules.system.service.SystemLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class FileRestoreServiceImpl implements FileRestoreService {
    private final SystemLogService systemLogService;

    public void restoreBackup(MultipartFile backupZip) throws IOException {
        Path targetDirPath = Paths.get(FileUtils.getSystemDir(SystemDir.UPLOAD));
        if (!Files.exists(targetDirPath)) {
            throw new IOException("Target directory not found: " + targetDirPath);
        }

        if (!vldRequestFile(backupZip)) {
            throw new BadRequestException("Zip file is invalid!");
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Path backupOldDir = targetDirPath.getParent().resolve(targetDirPath.getFileName() + "_" + timestamp);

        // 1. Đổi tên thư mục cũ
        FileUtils.renameDirectory(targetDirPath, backupOldDir);

        // 2. Lưu file zip upload
        Path uploadedZip = Paths.get(FileUtils.getSystemDir(SystemDir.RESTORE)).resolve(backupZip.getOriginalFilename());
        try (InputStream input = backupZip.getInputStream()) {
            Files.copy(input, uploadedZip, StandardCopyOption.REPLACE_EXISTING);
        }

        // 3. Giải nén file vào thư mục gốc mới
        Path newDir = targetDirPath;
        Files.createDirectories(newDir);

        boolean restoreStatus = false;

        try {
            FileUtils.unzipFile(uploadedZip, newDir);
            Files.move(backupOldDir, Paths.get(FileUtils.getSystemDir(SystemDir.ARCHIVE) + File.separator + backupOldDir.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            log.info("Restore successful for {}", targetDirPath);
            restoreStatus = true;
        } catch (Exception e) {
            log.error("Restore failed, rolling back...", e);
            rollbackRestore(newDir, backupOldDir, targetDirPath);
            restoreStatus = false;
            throw new IOException("Restore failed: " + e.getMessage(), e);
        } finally {
            String lvMessage = "%s, uploaded file name: " + backupZip.getOriginalFilename();
            systemLogService.writeLogUpdate(MODULE.SYSTEM, ACTION.SYS_DATA_RESTORE, MasterObject.FileStorage, "Restore data", String.format(lvMessage, restoreStatus ? "Success" : "Fail"));
        }
    }

    private void rollbackRestore(Path newDir, Path backupOldDir, Path targetDir) {
        try {
            FileUtils.deleteDirectory(newDir);
            FileUtils.renameDirectory(backupOldDir, targetDir);
            log.info("Rollback successful: restored old directory");
        } catch (Exception ex) {
            log.error("Rollback failed", ex);
        }
    }

    private boolean vldRequestFile(MultipartFile backupZip) {
        return true;
    }
}