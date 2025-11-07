package com.flowiee.pms.modules.media.service;

import com.flowiee.pms.common.base.service.ICurdService;
import org.springframework.web.multipart.MultipartFile;

import com.flowiee.pms.modules.media.entity.FileStorage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;

public interface FileStorageService extends ICurdService<FileStorage> {
    void saveFileAttach(MultipartFile multipartFile, Path dest) throws IOException;

    List<LinkedHashMap<String, String>> getSystemVolumes();
}