package com.flowiee.pms.media.service.impl;

import com.flowiee.pms.shared.base.FlwSys;
import com.flowiee.pms.shared.base.StartUp;
import com.flowiee.pms.shared.enums.SystemDir;
import com.flowiee.pms.shared.util.SysConfigUtils;
import com.flowiee.pms.media.entity.FileStorage;
import com.flowiee.pms.media.service.FileStorageService;
import com.flowiee.pms.system.entity.SystemConfig;
import com.flowiee.pms.shared.exception.AppException;
import com.flowiee.pms.shared.exception.BadRequestException;
import com.flowiee.pms.shared.exception.EntityNotFoundException;
import com.flowiee.pms.media.repository.FileStorageRepository;

import com.flowiee.pms.shared.util.CommonUtils;
import com.flowiee.pms.shared.util.FileUtils;
import com.flowiee.pms.system.enums.ConfigCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {
    private final FileStorageRepository mvFileRepository;

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
    public FileStorage create(FileStorage fileStorage) {
        FileStorage fileStorageSaved;
        try {
            vldAttachedFile(fileStorage.getFileAttach());
            vldResourceUploadPath(true);

            fileStorageSaved = mvFileRepository.save(fileStorage);
            Path pathDest = Paths.get(CommonUtils.getPathDirectory(fileStorageSaved.getModule().toUpperCase()) + File.separator + fileStorageSaved.getStorageName());

            saveFileAttach(fileStorage.getFileAttach(), pathDest);
            log.info("Uploaded 1 file: " + pathDest.toRealPath());
        } catch (IOException e) {
            throw new AppException(e);
        }
        return fileStorageSaved;
    }

    @Override
    public void saveFileAttach(MultipartFile multipartFile, Path dest) throws IOException {
        if (vldResourceUploadPath(true)) {
            multipartFile.transferTo(dest);
        }
    }

    @Override
    public boolean delete(Long fileId) {
        Optional<FileStorage> fileStorage = mvFileRepository.findById(fileId);
        if (fileStorage.isEmpty()) {
            throw new BadRequestException("File not found!");
        }
        mvFileRepository.deleteById(fileId);
        File file = new File(StartUp.getResourceUploadPath() + FileUtils.getImageUrl(fileStorage.get(), true));
        if (file.exists() && file.delete()) {
            log.info("Deleted file: {}", file.getAbsolutePath());
            return true;
        }
        return false;
    }

    @Override
    public List<LinkedHashMap<String, String>> getSystemVolumes() {
        List<LinkedHashMap<String, String>> lvVolumeMapList = new ArrayList<>();

        for (SystemDir lvSystemDir : SystemDir.values()) {
            LinkedHashMap<String, String> lvSysFolder = new LinkedHashMap<>();
            lvSysFolder.put("folder", lvSystemDir.getName());
            lvSysFolder.put("numberOfFiles", String.valueOf(lvSystemDir.getNumberOfFiles()));
            lvSysFolder.put("usedSpace", String.valueOf(lvSystemDir.getUsedSpace() / (1024 * 1024))); //MB
            lvSysFolder.put("note", "N/A");
            lvVolumeMapList.add(lvSysFolder);
        }

        return lvVolumeMapList;
    }

    private void vldAttachedFile(MultipartFile pUploadedFile) throws IOException {
        FileUtils.isAllowUpload(FileUtils.getFileExtension(pUploadedFile.getOriginalFilename()), true, null);

        SystemConfig lvFileSizeUploadCnf = FlwSys.getSystemConfigs().get(ConfigCode.maxSizeFileUpload);
        BigDecimal lvMaximumSizeMBAllowToUpload = SysConfigUtils.isValid(lvFileSizeUploadCnf) ?
                new BigDecimal(lvFileSizeUploadCnf.getIntValue()) : new BigDecimal(2);

        SystemConfig lvVolumeConfig = FlwSys.getSystemConfigs().get(ConfigCode.resourceVolume);
        if (SysConfigUtils.isValid(lvVolumeConfig)) {
            BigDecimal lvMaximumVolumeMB = BigDecimal.valueOf(SysConfigUtils.getIntValue(lvVolumeConfig))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal lvSizeAttachedMB = BigDecimal.valueOf(pUploadedFile.getSize()).divide(new BigDecimal((1024 * 1024)))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal lvSystemDirByteSize = BigDecimal.valueOf(Arrays.stream(SystemDir.values())
                    .mapToDouble(SystemDir::getUsedSpace)
                    .sum()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal lvSystemDirMBSize = lvSystemDirByteSize.divide(new BigDecimal(1024 * 1024))
                    .setScale(2, RoundingMode.HALF_UP);

            if (lvSizeAttachedMB.compareTo(lvMaximumSizeMBAllowToUpload) >= 0) {
                throw new IOException("Upload failed: File's size is over the configuration, " + lvMaximumSizeMBAllowToUpload.toPlainString());
            }

            if (lvSystemDirMBSize.add(lvSizeAttachedMB).compareTo(lvMaximumVolumeMB)  >= 0) {
                log.error("Maximum volume: {}MB; system space used: {}MB; uploaded file's size: {}MB",
                        lvMaximumVolumeMB, lvSystemDirMBSize, lvSizeAttachedMB);
                throw new IOException("Upload failed: server storage is full. Please try again later or remove unnecessary files.");
            }
        }
    }

    private boolean vldResourceUploadPath(boolean throwException) {
        if (StartUp.getResourceUploadPath() == null) {
            SystemConfig resourceUploadPathConfig = FlwSys.getSystemConfigs().get(ConfigCode.resourceUploadPath);
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