package com.flowiee.pms.common.base.service;

import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.ExcelUtils;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.modules.system.model.EximResult;
import com.flowiee.pms.modules.system.service.ExportService;
import com.flowiee.pms.common.enumeration.TemplateExport;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.util.List;

public abstract class BaseExportService implements ExportService {
    protected abstract void prepareData(Object pCondition, boolean pTemplateOnly);
    protected abstract void writeData(Object pCondition);

    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected XSSFSheet mvDataSheet;
    protected String mvDataSheetName = "data";
    protected int mvHeadKeyLine = 1;
    protected int mvDataBeginLine = 3;

    protected XSSFWorkbook mvWorkbook;
    protected XSSFSheet    mvCategoriesHiddenSheet;
    protected EximResult   mvEximResult;
    protected String       mvHiddenSheetName = "CategoriesHiddenSheet";
    protected int          mvDataExportSize = 0;

    @Override
    public EximResult exportToExcel(TemplateExport templateExport, Object pCondition, boolean templateOnly) {
        try {
            mvEximResult = new EximResult(templateExport);

            mvWorkbook = getWorkbook(templateExport);

            mvDataSheet = mvWorkbook.getSheet(mvDataSheetName) == null
                    ? mvWorkbook.createSheet(mvDataSheetName)
                    : mvWorkbook.getSheet(mvDataSheetName);
            mvWorkbook.setActiveSheet(mvWorkbook.getSheetIndex(mvDataSheet));

            mvCategoriesHiddenSheet = mvWorkbook.getSheet(mvHiddenSheetName) == null
                    ? mvWorkbook.createSheet(mvHiddenSheetName)
                    : mvWorkbook.getSheet(mvHiddenSheetName);
            mvWorkbook.setSheetHidden(mvWorkbook.getSheetIndex(mvCategoriesHiddenSheet), true);

            prepareData(pCondition, templateOnly);
            writeData(pCondition);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            mvWorkbook.write(byteArrayOutputStream);
            setFileContent(byteArrayOutputStream);
            setHttpHeaders();
            mvEximResult.setResultStatus("OK");

            return mvEximResult;
        } catch (Exception e) {
            mvEximResult.setResultStatus("NOK");
            logger.error("Error when export data!", e);
            throw new AppException("Error when export data!", e);
        } finally {
            try {
                if (mvWorkbook != null) mvWorkbook.close();
                Files.deleteIfExists(mvEximResult.getPathTarget());
                mvEximResult.setFinishTime(LocalTime.now());
            } catch (IOException e) {
                logger.error("Error when export data!", e);
            }
        }
    }

    private XSSFWorkbook getWorkbook(TemplateExport pTemplateExport) throws IOException, InvalidFormatException {
        Path lvPathSource = mvEximResult.getPathSource();
        Path lvPathTarget = mvEximResult.getPathTarget();

        //Docker environment
        if (!Files.exists(lvPathSource)) {
            try (InputStream in = new ClassPathResource(
                    "static/templates/excel/" + pTemplateExport.getTemplateName()
            ).getInputStream()) {
                Files.copy(in, lvPathTarget, StandardCopyOption.REPLACE_EXISTING);
            }
        } else {
            Files.copy(lvPathSource, lvPathTarget, StandardCopyOption.REPLACE_EXISTING);
        }

        return new XSSFWorkbook(lvPathTarget.toFile());
    }

    private void setHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + mvEximResult.getDefaultOutputName());
        mvEximResult.setHttpHeaders(httpHeaders);
    }

    private void setFileContent(ByteArrayOutputStream byteArrayOS) {
        ByteArrayInputStream byteArrayIS = new ByteArrayInputStream(byteArrayOS.toByteArray());
        InputStreamResource inputStreamResource = new InputStreamResource(byteArrayIS);
        mvEximResult.setContent(inputStreamResource);
    }

    protected void setBorderCell(XSSFRow pRow, int pColFrom, int pColTo) {
        if (pRow == null) return;

        for (int j = pColFrom; j <= pColTo; j++) {
            XSSFCell lvCell = pRow.getCell(j);
            if (lvCell == null)
                continue;

            XSSFCellStyle lvCellStyle = mvWorkbook.createCellStyle();
            lvCellStyle.setBorderTop(BorderStyle.THIN);
            lvCellStyle.setBorderBottom(BorderStyle.THIN);
            lvCellStyle.setBorderLeft(BorderStyle.THIN);
            lvCellStyle.setBorderRight(BorderStyle.THIN);

            lvCell.setCellStyle(lvCellStyle);
        }
    }

    protected int getDataEndAtLine() {
        return mvDataBeginLine + (mvDataExportSize > 0 ? mvDataExportSize - 1 : 0);
    }

    protected void createDropdownList(List<String> pListValue, String pHeadKey) {
        int lvColumnIndex = -1;
        XSSFRow lvRowHead = getRowHeadKey();
        for (int i = 0; i < lvRowHead.getPhysicalNumberOfCells(); i++) {
            XSSFCell lvCell = lvRowHead.getCell(i);
            if (lvCell != null && pHeadKey.equals(CoreUtils.trim(lvCell.getStringCellValue()))) {
                lvColumnIndex = i;
                break;
            }
        }
        int lvDataEndAtLine = getDataEndAtLine();
        String lvNameName = pHeadKey;
        ExcelUtils.createDropdownList(mvWorkbook, mvDataSheet, mvCategoriesHiddenSheet, pListValue, mvDataBeginLine, lvDataEndAtLine, lvColumnIndex, lvNameName);
    }

    protected XSSFRow getRowHeadKey() {
        return mvDataSheet.getRow(mvHeadKeyLine);
    }
}