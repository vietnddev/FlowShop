package com.flowiee.pms.base.service;

import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.entity.system.ImportHistory;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.model.EximResult;
import com.flowiee.pms.repository.system.AppImportRepository;
import com.flowiee.pms.service.ImportService;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.enumeration.TemplateExport;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseImportService extends BaseService implements ImportService {
    protected abstract void writeData() throws AppException, IOException;

    @Autowired
    protected AppImportRepository mvFileImportRepository;

    protected XSSFWorkbook  mvWorkbook;
    protected EximResult    mvEximResult;
    protected ImportHistory mvImportHistory;

    @Transactional
    @Override
    public EximResult importFromExcel(TemplateExport templateExport, MultipartFile multipartFile) {
        try {
            preImport(templateExport, multipartFile);

            mvEximResult = new EximResult(templateExport);
            mvWorkbook = new XSSFWorkbook(multipartFile.getInputStream());

            writeData();

            Path lvPath = getPath(templateExport, multipartFile);
            multipartFile.transferTo(lvPath);

            ImportHistory lvImportInfoMdl = mvFileImportRepository.save(ImportHistory.builder()
                    .module(templateExport.getModule().name())
                    .entity(templateExport.getEntity())
                    .beginTime(mvEximResult.getBeginTime())
                    .finishTime(LocalTime.now())
                    .filePath(lvPath.toString())
                    .account(CommonUtils.getUserPrincipal().toEntity())
                    .build());

            if ("NOK".equals(mvEximResult.getResultStatus())) {
                setFileContentError();
            }

            postImport(templateExport, lvImportInfoMdl);

            return mvEximResult;
        } catch (Exception e) {
            mvEximResult.setResultStatus("NOK");
            logger.error("Error when import data!" + e.getMessage(), e);
            throw new AppException("Error when import data!", e);
        } finally {
            try {
                if (mvWorkbook != null) mvWorkbook.close();
                Files.deleteIfExists(mvEximResult.getPathTarget());
                mvEximResult.setFinishTime(mvImportHistory.getFinishTime());
            } catch (IOException e) {
                logger.error("Error when import data!", e);
            }
            if (CoreUtils.isNullStr(mvEximResult.getResultStatus())) {
                mvEximResult.setResultStatus("OK");
            }
        }
    }

    public void preImport(TemplateExport pTemplateExport, MultipartFile pMultipartFile) throws AppException {

    }

    public void postImport(TemplateExport pTemplateExport, ImportHistory pImportInfoMdl) throws AppException {

    }

    public XSSFWorkbook getWorkbook() {
        return mvWorkbook;
    }

    public void setData(Object pObj) {
        mvEximResult.setData(pObj);
    }

    public Path getPath(TemplateExport templateExport, MultipartFile multipartFile) {
        String lvPathStorage = CommonUtils.getPathDirectory(templateExport.getModule());
        long lvBeginTime = mvEximResult.getBeginTime().toNanoOfDay();
        String lvOriginalFileImportName = multipartFile.getOriginalFilename();
        return Paths.get(lvPathStorage + File.separator + lvBeginTime + "_" + lvOriginalFileImportName);
    }

    public void highlightCellInvalidValue(XSSFCell pCell) {
        XSSFFont lvFontStyle = mvWorkbook.createFont();
        lvFontStyle.setColor(IndexedColors.RED.getIndex());

        XSSFCellStyle lvCellStyle = mvWorkbook.createCellStyle();
        lvCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        lvCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        lvCellStyle.setFont(lvFontStyle);

        pCell.setCellStyle(lvCellStyle);
    }

    public Object getCellValue(XSSFCell pCell) {
        if (pCell == null) {
            return "";
        }
        return switch (pCell.getCellType()) {
            case STRING -> CoreUtils.trim(pCell.getStringCellValue());
            case NUMERIC -> {
                double numericValue = pCell.getNumericCellValue();
                yield (numericValue == Math.floor(numericValue)) ? (int) numericValue : numericValue;
            }
            case BOOLEAN -> pCell.getBooleanCellValue();
            default -> "";
        };
    }

    public void setFileContentError() throws IOException {
        ByteArrayOutputStream lvByteArrayOutputStream = new ByteArrayOutputStream();
        mvWorkbook.write(lvByteArrayOutputStream);

        ByteArrayInputStream lvByteArrayInputStream = new ByteArrayInputStream(lvByteArrayOutputStream.toByteArray());
        InputStreamResource lvInputStreamResource = new InputStreamResource(lvByteArrayInputStream);
        mvEximResult.setContent(lvInputStreamResource);

        HttpHeaders lvHttpHeaders = new HttpHeaders();
        lvHttpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        lvHttpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + mvEximResult.getDefaultOutputName());
        mvEximResult.setHttpHeaders(lvHttpHeaders);
    }
}