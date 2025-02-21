package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.base.service.BaseImportService;
import com.flowiee.pms.common.enumeration.TemplateExport;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.entity.system.ImportHistory;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.model.dto.ProductDTO;
import com.flowiee.pms.repository.product.ProductDetailRepository;
import com.flowiee.pms.repository.product.ProductRepository;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductImportServiceImpl extends BaseImportService {
    ProductDetailRepository productDetailRepository;
    ProductRepository productRepository;

    @Override
    public void preImport(TemplateExport pTemplateExport, MultipartFile pMultipartFile) throws AppException {
        super.preImport(pTemplateExport, pMultipartFile);
    }

    @Override
    protected void writeData() throws AppException {
        List<ProductDTO> lvData = new ArrayList<>();

        XSSFWorkbook lvWorkbook = getWorkbook();
        XSSFSheet lvSheet = lvWorkbook.getSheetAt(0);
        int lvDataBeginLine = 3;
        for (int i = lvDataBeginLine; i < lvSheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow lvRow = lvSheet.getRow(i);
            if (lvRow == null) {
                continue;
            }

            String productName = "";
            String productCode = "";
            String productTypeCode = "";
            String brandCode = "";
            String unitCode = "";
            String colorCode = "";
            String sizeCode = "";
            String fabricTypeCode = "";
            Integer inventoryQty = CoreUtils.coalesce(0);
            Integer soldQty = CoreUtils.coalesce(0);
            Integer defectiveQty = CoreUtils.coalesce(0);
            String productStatus = "";

            String categoryCode = lvRow.getCell(1).getStringCellValue();
            String categoryName = lvRow.getCell(2).getStringCellValue();
            String categoryNote = lvRow.getCell(3).getStringCellValue();
            if (categoryName == null || categoryName.isEmpty()) {
                XSSFCellStyle cellStyle = lvWorkbook.createCellStyle();
                XSSFFont fontStyle = lvWorkbook.createFont();
                lvRow.getCell(1).setCellStyle(CommonUtils.highlightDataImportError(cellStyle, fontStyle));
                lvRow.getCell(2).setCellStyle(CommonUtils.highlightDataImportError(cellStyle, fontStyle));
                lvRow.getCell(3).setCellStyle(CommonUtils.highlightDataImportError(cellStyle, fontStyle));
                continue;
            }

//            lvListToImport.add(Category.builder()
//                    .type(null)
//                    .code(!categoryCode.isEmpty() ? categoryCode : CommonUtils.genCategoryCodeByName(categoryName))
//                    .name(categoryName)
//                    .note(categoryNote)
//                    .build());
        }

//        List<Category> listCategorySaved = mvCategoryRepository.saveAll(lvListToImport);
//        mvImportHistory.setTotalRecord(listCategorySaved.size());
//        mvImportHistory.setResult("OK");

        setData(lvData);
    }

    @Override
    public void postImport(TemplateExport pTemplateExport, ImportHistory pImportInfoMdl) throws AppException {
        super.postImport(pTemplateExport, pImportInfoMdl);
    }
}