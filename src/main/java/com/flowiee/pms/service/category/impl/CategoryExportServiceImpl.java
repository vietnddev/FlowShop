package com.flowiee.pms.service.category.impl;

import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.base.service.BaseExportService;
import com.flowiee.pms.service.category.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryExportServiceImpl extends BaseExportService {
    CategoryService mvCategoryService;

    @Override
    protected void writeData(Object pCondition) {
        XSSFSheet sheet = mvWorkbook.getSheetAt(0);
        List<Category> listData = mvCategoryService.findAll();
        for (int i = 0; i < listData.size(); i++) {
            Category model = listData.get(i);
            
            XSSFRow row = sheet.createRow(i + 3);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(model.getCode());
            row.createCell(2).setCellValue(model.getName());
            row.createCell(3).setCellValue(model.getNote());

            setBorderCell(row, 0, 3);
        }
    }
}