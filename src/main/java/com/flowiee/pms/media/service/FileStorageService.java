package com.flowiee.pms.media.service;

import com.flowiee.pms.media.entity.FileStorage;
import com.flowiee.pms.shared.base.CreateService;
import com.flowiee.pms.shared.base.DeleteService;
import com.flowiee.pms.shared.base.FindService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;

public interface FileStorageService extends FindService<FileStorage>, CreateService<FileStorage>, DeleteService {
    void saveFileAttach(MultipartFile multipartFile, Path dest) throws IOException;

    List<LinkedHashMap<String, String>> getSystemVolumes();
}