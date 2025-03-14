package com.flowiee.pms.service.category.impl;

import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.base.BaseExportService;
import com.flowiee.pms.repository.category.CategoryRepository;
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
    CategoryRepository mvCategoryRepository;

    @Override
    protected void prepareData(Object pCondition, boolean pTemplateOnly) {

    }

    @Override
    protected void writeData(Object pCondition) {
        XSSFSheet sheet = mvWorkbook.getSheetAt(0);
        List<Category> listData = mvCategoryRepository.findAll();
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