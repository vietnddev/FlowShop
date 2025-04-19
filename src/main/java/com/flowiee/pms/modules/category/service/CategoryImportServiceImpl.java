package com.flowiee.pms.modules.category.service;

import com.flowiee.pms.modules.category.entity.Category;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.modules.category.repository.CategoryRepository;
import com.flowiee.pms.common.base.service.BaseImportService;
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
    protected void writeData() throws AppException {
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
                        row.getCell(1).setCellStyle(CommonUtils.highlightCellInvalidValue(cellStyle, fontStyle));
                        row.getCell(2).setCellStyle(CommonUtils.highlightCellInvalidValue(cellStyle, fontStyle));
                        row.getCell(3).setCellStyle(CommonUtils.highlightCellInvalidValue(cellStyle, fontStyle));
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
        } catch (Exception ex) {
        }
    }

    @Override
    public String approveData() {
        return null;
    }
}