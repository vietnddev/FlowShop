package com.flowiee.pms.service.storage.impl;

import com.flowiee.pms.entity.storage.Storage;
import com.flowiee.pms.model.StorageItems;
import com.flowiee.pms.model.dto.StorageDTO;
import com.flowiee.pms.base.service.BaseExportService;
import com.flowiee.pms.service.storage.StorageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StorageExportServiceImpl extends BaseExportService {
    StorageService mvStorageService;

    private DateTimeFormatter DF_DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void prepareData(Object pCondition, boolean pTemplateOnly) {

    }

    @Override
    protected void writeData(Object pCondition) {
        Storage lvCondition = (Storage) pCondition;

        StorageDTO storage = mvStorageService.findById(lvCondition.getId(), false);
        if (storage == null)
            return;

        XSSFSheet sheet = mvWorkbook.getSheetAt(0);

        XSSFCell cellTitleStorage = sheet.getRow(1).getCell(0);
        cellTitleStorage.setCellValue(cellTitleStorage.getStringCellValue().replace("{storageName}", storage.getName()));

        List<StorageItems> listData = mvStorageService.findStorageItems( -1, -1, storage.getId(), null).getContent();
        for (int i = 0; i < listData.size(); i++) {
            StorageItems storageItems = listData.get(i);

            XSSFRow row = sheet.createRow(i + 3);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(storageItems.getItemName());
            row.createCell(2).setCellValue(storageItems.getIsProduct().equals("Y") ? "Sản phẩm" : "Nguyên vật liệu");
            row.createCell(3).setCellValue(storageItems.getItemBrand());
            row.createCell(4).setCellValue(storageItems.getItemSalesAvailableQty());
            row.createCell(5).setCellValue(storageItems.getItemStorageQty());
            row.createCell(6).setCellValue(storageItems.getLastImportTime().format(DF_DDMMYYYY));
            row.createCell(7).setCellValue(storageItems.getFirstImportTime().format(DF_DDMMYYYY));

            setBorderCell(row, 0, 7);
        }
    }
}