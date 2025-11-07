package com.flowiee.pms.modules.system.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileRestoreService {
    void restoreBackup(MultipartFile backupZip) throws IOException;
}