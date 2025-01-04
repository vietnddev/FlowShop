package com.flowiee.pms.service.category.impl;

import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.repository.category.CategoryRepository;
import com.flowiee.pms.base.service.BaseImportService;
import com.flowiee.pms.common.utils.CommonUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryImportServiceImpl extends BaseImportService {
    CategoryRepository mvCategoryRepository;

    @Override
    protected void writeData() {
        List<Category> lvListToImport = new ArrayList<>();
        try {
            XSSFSheet sheet = mvWorkbook.getSheetAt(0);
            for (int i = 3; i < sheet.getPhysicalNumberOfRows(); i++) {
                XSSFRow row = sheet.getRow(i);
                if (row != null) {
                    String categoryCode = row.getCell(1).getStringCellValue();
                    String categoryName = row.getCell(2).getStringCellValue();
                    String categoryNote = row.getCell(3).getStringCellValue();
                    if (categoryName == null || categoryName.isEmpty()) {
                        XSSFCellStyle cellStyle = mvWorkbook.createCellStyle();
                        XSSFFont fontStyle = mvWorkbook.createFont();
                        row.getCell(1).setCellStyle(CommonUtils.highlightDataImportError(cellStyle, fontStyle));
                        row.getCell(2).setCellStyle(CommonUtils.highlightDataImportError(cellStyle, fontStyle));
                        row.getCell(3).setCellStyle(CommonUtils.highlightDataImportError(cellStyle, fontStyle));
                        continue;
                    }
                    lvListToImport.add(Category.builder()
                            .type(null)
                            .code(!categoryCode.isEmpty() ? categoryCode : CommonUtils.genCategoryCodeByName(categoryName))
                            .name(categoryName)
                            .note(categoryNote)
                            .build());
                }
            }
            List<Category> listCategorySaved = mvCategoryRepository.saveAll(lvListToImport);
            mvFileImportHistory.setTotalRecord(listCategorySaved.size());
            mvFileImportHistory.setResult("OK");
        } catch (Exception ex) {
            mvFileImportHistory.setResult("NOK");
        }
    }
}