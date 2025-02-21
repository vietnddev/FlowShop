package com.flowiee.pms.base.service;

import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.model.EximResult;
import com.flowiee.pms.service.ExportService;
import com.flowiee.pms.common.enumeration.TemplateExport;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;

public abstract class BaseExportService extends BaseService implements ExportService {
    protected abstract void writeData(Object pCondition);

    protected XSSFWorkbook mvWorkbook;
    protected EximResult   mvEximResult;

    @Override
    public EximResult exportToExcel(TemplateExport templateExport, Object pCondition, boolean templateOnly) {
        try {
            mvEximResult = new EximResult(templateExport);
            mvWorkbook = new XSSFWorkbook(Files.copy(mvEximResult.getPathSource(), mvEximResult.getPathTarget(), StandardCopyOption.REPLACE_EXISTING).toFile());
            if (!templateOnly) {
                writeData(pCondition);
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            mvWorkbook.write(byteArrayOutputStream);
            setFileContent(byteArrayOutputStream);
            setHttpHeaders();
            mvEximResult.setResult("OK");
            return mvEximResult;
        } catch (Exception e) {
            mvEximResult.setResult("NOK");
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
            XSSFCellStyle lvCellStyle = mvWorkbook.createCellStyle();
            lvCellStyle.setBorderTop(BorderStyle.THIN);
            lvCellStyle.setBorderBottom(BorderStyle.THIN);
            lvCellStyle.setBorderLeft(BorderStyle.THIN);
            lvCellStyle.setBorderRight(BorderStyle.THIN);

            pRow.getCell(j).setCellStyle(lvCellStyle);
        }
    }
}