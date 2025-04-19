package com.flowiee.pms.modules.media.service;

import com.flowiee.pms.common.base.service.BaseCurdService;
import org.springframework.web.multipart.MultipartFile;

import com.flowiee.pms.modules.media.entity.FileStorage;

import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService extends BaseCurdService<FileStorage> {
    String saveFileOfImport(MultipartFile fileImport, FileStorage fileInfo) throws IOException;

    void saveFileAttach(MultipartFile multipartFile, Path dest) throws IOException;
}