package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.base.service.BaseExportService;
import com.flowiee.pms.common.utils.DateTimeUtil;
import com.flowiee.pms.modules.inventory.dto.TransactionGoodsDTO;
import com.flowiee.pms.modules.inventory.model.TransactionGoodsReq;
import com.flowiee.pms.modules.inventory.service.TransactionGoodsService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionGoodsExportServiceImpl extends BaseExportService {
    private final TransactionGoodsService transactionGoodsService;

    @Override
    protected void prepareData(Object pCondition, boolean pTemplateOnly) {

    }

    @Override
    protected void writeData(Object pCondition) {
        TransactionGoodsDTO lvCondition = (TransactionGoodsDTO) pCondition;

        List<TransactionGoodsDTO> lvData = transactionGoodsService.getTransactionGoods(lvCondition.getTransactionType(),
                TransactionGoodsReq.builder()
                        .pageSize(Integer.MAX_VALUE)
                        .pageNum(0)
                        .build())
                .getContent();

        XSSFSheet sheet = mvWorkbook.getSheetAt(0);

        //XSSFCell cellTitleStorage = sheet.getRow(1).getCell(0);
        //cellTitleStorage.setCellValue(cellTitleStorage.getStringCellValue().replace("{storageName}", storage.getName()));

        for (int i = 0; i < lvData.size(); i++) {
            TransactionGoodsDTO lvTranGoods = lvData.get(i);

            XSSFRow row = sheet.createRow(i + 3);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(lvTranGoods.getTransactionType().name());
            row.createCell(2).setCellValue(lvTranGoods.getTitle());
            row.createCell(3).setCellValue(lvTranGoods.getWarehouse().getName());
            row.createCell(4).setCellValue(DateTimeUtil.format(lvTranGoods.getCreatedAt(), DateTimeUtil.FORMAT_DATE));
            row.createCell(5).setCellValue(lvTranGoods.getTransactionStatus().name());

            setBorderCell(row, 0, 5);
        }
    }
}