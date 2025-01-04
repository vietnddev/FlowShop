package com.flowiee.pms.service.system;

import com.flowiee.pms.base.service.BaseCurdService;
import org.springframework.web.multipart.MultipartFile;

import com.flowiee.pms.entity.system.FileStorage;

import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService extends BaseCurdService<FileStorage> {
    String saveFileOfImport(MultipartFile fileImport, FileStorage fileInfo) throws IOException;

    void saveFileAttach(MultipartFile multipartFile, Path dest) throws IOException;
}