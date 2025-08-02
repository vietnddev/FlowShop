package com.flowiee.pms.modules.media.service.impl;

import com.flowiee.pms.common.base.StartUp;
import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.modules.media.service.FileStorageService;
import com.flowiee.pms.modules.system.entity.SystemConfig;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.common.exception.EntityNotFoundException;
import com.flowiee.pms.modules.system.repository.ConfigRepository;
import com.flowiee.pms.modules.media.repository.FileStorageRepository;

import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.common.enumeration.ConfigCode;
import com.flowiee.pms.common.enumeration.ErrorCode;
import com.flowiee.pms.common.enumeration.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {
    FileStorageRepository mvFileRepository;
    ConfigRepository mvConfigRepository;

    @Override
    public FileStorage findById(Long fileId, boolean pThrowException) {
        Optional<FileStorage> entityOptional = mvFileRepository.findById(fileId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"file model"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Transactional
    @Override
    public FileStorage save(FileStorage fileStorage) {
        FileUtils.isAllowUpload(fileStorage.getExtension(), true, null);
        vldResourceUploadPath(true);
        FileStorage fileStorageSaved = mvFileRepository.save(fileStorage);
        Path pathDest = Paths.get(CommonUtils.getPathDirectory(fileStorageSaved.getModule().toUpperCase()) + File.separator + fileStorageSaved.getStorageName());
        try {
            saveFileAttach(fileStorage.getFileAttach(), pathDest);
        } catch (IOException ex) {
            throw new AppException("An error occurred while saving the attachment!", ex);
        }

        return fileStorageSaved;
    }

    @Override
    public FileStorage update(FileStorage entity, Long entityId) {
        throw new AppException("Method does not support!");
    }

    @Override
    public String saveFileOfImport(MultipartFile fileImport, FileStorage fileInfo) throws IOException {
        mvFileRepository.save(fileInfo);
        Path dest = Paths.get(CommonUtils.getPathDirectory(fileInfo.getModule()) + "/" + "I_" + fileInfo.getStorageName());
        saveFileAttach(fileImport, dest);
        return "OK";
    }

    @Override
    public void saveFileAttach(MultipartFile multipartFile, Path dest) throws IOException {
        if (vldResourceUploadPath(true)) {
            multipartFile.transferTo(dest);
        }
    }

    @Override
    public String delete(Long fileId) {
        Optional<FileStorage> fileStorage = mvFileRepository.findById(fileId);
        if (fileStorage.isEmpty()) {
            throw new BadRequestException("File not found!");
        }
        mvFileRepository.deleteById(fileId);
        File file = new File(StartUp.getResourceUploadPath() + FileUtils.getImageUrl(fileStorage.get(), true));
        if (file.exists() && file.delete()) {
            return MessageCode.DELETE_SUCCESS.getDescription();
        }
        return String.format(ErrorCode.DELETE_ERROR_OCCURRED.getDescription(), "file");
    }

    private boolean vldResourceUploadPath(boolean throwException) {
        if (StartUp.getResourceUploadPath() == null) {
            SystemConfig resourceUploadPathConfig = mvConfigRepository.findByCode(ConfigCode.resourceUploadPath.name());
            if (SysConfigUtils.isValid(resourceUploadPathConfig)) {
                StartUp.mvResourceUploadPath = resourceUploadPathConfig.getValue();
                return true;
            } else {
                if (throwException) {
                    throw new AppException("The uploaded file saving directory is not configured, please try again later!");
                } else {
                    return false;
                }
            }
        }
        return true;
    }
}