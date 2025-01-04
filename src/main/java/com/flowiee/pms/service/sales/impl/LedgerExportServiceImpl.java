package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.entity.sales.LedgerTransaction;
import com.flowiee.pms.model.GeneralLedger;
import com.flowiee.pms.base.service.BaseExportService;
import com.flowiee.pms.service.sales.LedgerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LedgerExportServiceImpl extends BaseExportService {
    LedgerService mvLedgerService;

    @Override
    protected void writeData(Object pCondition) {
        GeneralLedger lvData = mvLedgerService.findGeneralLedger(-1, -1, null, null);
        XSSFSheet lvSheet = mvWorkbook.getSheetAt(0);
        for (int i = 0; i < lvData.getListTransactions().size(); i++) {
            LedgerTransaction t = lvData.getListTransactions().get(i);

            XSSFRow row = lvSheet.createRow(i + 3);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(t.getTranCode());
            row.createCell(2).setCellValue(t.getTranType());
            row.createCell(3).setCellValue(t.getDescription());
            row.createCell(4).setCellValue(t.getAmount().toPlainString());
            row.createCell(5).setCellValue(t.getCreatedAt());
            row.createCell(6).setCellValue(t.getCreatedBy());
            row.createCell(7).setCellValue(t.getFromToName());
            row.createCell(8).setCellValue(t.getGroupObjectName());
            row.createCell(9).setCellValue("");

            setBorderCell(row, 0, 9);
        }
    }
}