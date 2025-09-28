package com.flowiee.pms.modules.system.service.impl;

import com.flowiee.pms.common.utils.DateTimeUtil;
import com.flowiee.pms.modules.system.service.SystemLogService;
import com.flowiee.pms.modules.system.entity.SystemLog;
import com.flowiee.pms.common.base.service.BaseExportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogExportServiceImpl extends BaseExportService {
    private final SystemLogService mvSystemLogService;

    @Override
    protected void prepareData(Object pCondition, boolean pTemplateOnly) {

    }

    @Override
    protected void writeData(Object pCondition) {
        XSSFSheet sheet = mvWorkbook.getSheetAt(0);

        List<SystemLog> listData = mvSystemLogService.findAll(Integer.MAX_VALUE, 0, null, null, null).getContent();
        for (int i = 0; i < listData.size(); i++) {
            SystemLog systemLog = listData.get(i);

            XSSFRow row = sheet.createRow(i + 3);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(systemLog.getAccountName());
            row.createCell(2).setCellValue(systemLog.getTitle());
            row.createCell(3).setCellValue(systemLog.getContent());
            row.createCell(4).setCellValue(systemLog.getContentChange());
            row.createCell(5).setCellValue(DateTimeUtil.format(systemLog.getCreatedAt(), DateTimeUtil.FORMAT_DATE_TIME));
            row.createCell(6).setCellValue(systemLog.getIp());

            setBorderCell(row, 0, 6);
        }
    }
}