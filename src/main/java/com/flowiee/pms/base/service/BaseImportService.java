package com.flowiee.pms.base.service;

import com.flowiee.pms.entity.system.FileImportHistory;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.model.EximModel;
import com.flowiee.pms.repository.system.AppImportRepository;
import com.flowiee.pms.service.ImportService;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.enumeration.TemplateExport;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseImportService extends BaseService implements ImportService {
    protected abstract void writeData();

    @Autowired
    AppImportRepository         mvFileImportRepository;
    protected XSSFWorkbook      mvWorkbook;
    protected EximModel         mvEximModel;
    protected FileImportHistory mvFileImportHistory;

    @Transactional
    @Override
    public EximModel importFromExcel(TemplateExport templateExport, MultipartFile multipartFile) {
        try {
            mvEximModel = new EximModel(templateExport);
            mvWorkbook = new XSSFWorkbook(multipartFile.getInputStream());
            writeData();

            Path path = Paths.get(CommonUtils.getPathDirectory(templateExport.getModule()) + "/" + mvEximModel.getBeginTime().toNanoOfDay() + "_" + multipartFile.getOriginalFilename());
            multipartFile.transferTo(path);

            mvFileImportRepository.save(FileImportHistory.builder()
                    .module(templateExport.getModule().name())
                    .entity(templateExport.getEntity())
                    .beginTime(mvEximModel.getBeginTime())
                    .finishTime(LocalTime.now())
                    .filePath(path.toString())
                    .account(CommonUtils.getUserPrincipal().toEntity())
                    .build());

            mvEximModel.setResult("OK");
            return mvEximModel;
        } catch (Exception e) {
            mvEximModel.setResult("NOK");
            throw new AppException("Error when import data!", e);
        } finally {
            try {
                if (mvWorkbook != null) mvWorkbook.close();
                Files.deleteIfExists(mvEximModel.getPathTarget());
                mvEximModel.setFinishTime(mvFileImportHistory.getFinishTime());
            } catch (IOException e) {
                logger.error("Error when import data!", e);
            }
        }
    }
}