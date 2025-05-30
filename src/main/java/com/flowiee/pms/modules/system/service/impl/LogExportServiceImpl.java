package com.flowiee.pms.modules.system.service.impl;

import com.flowiee.pms.modules.system.service.SystemLogService;
import com.flowiee.pms.modules.system.entity.SystemLog;
import com.flowiee.pms.common.base.service.BaseExportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LogExportServiceImpl extends BaseExportService {
    SystemLogService mvSystemLogService;

    private DateTimeFormatter mvDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    protected void prepareData(Object pCondition, boolean pTemplateOnly) {

    }

    @Override
    protected void writeData(Object pCondition) {
        XSSFSheet sheet = mvWorkbook.getSheetAt(0);

        List<SystemLog> listData = mvSystemLogService.findAll(-1, -1).getContent();
        for (int i = 0; i < listData.size(); i++) {
            SystemLog systemLog = listData.get(i);

            XSSFRow row = sheet.createRow(i + 3);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(systemLog.getAccountName());
            row.createCell(2).setCellValue(systemLog.getTitle());
            row.createCell(3).setCellValue(systemLog.getContent());
            row.createCell(4).setCellValue(systemLog.getContentChange());
            row.createCell(5).setCellValue(systemLog.getCreatedAt().format(mvDateTimeFormatter));
            row.createCell(6).setCellValue(systemLog.getIp());

            setBorderCell(row, 0, 6);
        }
    }
}