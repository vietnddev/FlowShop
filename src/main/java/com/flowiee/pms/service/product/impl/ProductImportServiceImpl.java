package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.base.service.BaseImportService;
import com.flowiee.pms.common.enumeration.CATEGORY;
import com.flowiee.pms.common.enumeration.TemplateExport;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.entity.product.ProductTemp;
import com.flowiee.pms.entity.product.ProductVariantTemp;
import com.flowiee.pms.entity.system.ImportHistory;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.repository.category.CategoryRepository;
import com.flowiee.pms.repository.product.ProductTempRepository;
import com.flowiee.pms.repository.product.ProductVariantTempRepository;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductImportServiceImpl extends BaseImportService {
    ProductVariantTempRepository productVariantTempRepository;
    ProductTempRepository productTempRepository;
    CategoryRepository categoryRepository;

    @Override
    public void preImport(TemplateExport pTemplateExport, MultipartFile pMultipartFile) throws AppException {
        super.preImport(pTemplateExport, pMultipartFile);
    }

    @Override
    protected void writeData() throws AppException, IOException {
        List<ProductTemp> lvData = new ArrayList<>();

        StringBuilder lvMessageError = new StringBuilder();
        int lvDataBeginLine = 1;
        int lvHeadKeyLine = 0;

        XSSFSheet lvSheet = mvWorkbook.getSheetAt(0);
        for (int i = lvDataBeginLine; i < lvSheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow lvRowHead = lvSheet.getRow(lvHeadKeyLine);
            XSSFRow lvRowData = lvSheet.getRow(i);
            if (lvRowData == null) {
                continue;
            }

            ProductTemp productTemp = new ProductTemp();
            ProductVariantTemp productVariantTemp = new ProductVariantTemp();
            for (int j = 0; j < lvRowHead.getPhysicalNumberOfCells(); j++) {
                if (lvRowHead.getCell(j) == null) {
                    continue;
                }

                String lvHeaderKey = CoreUtils.trim(lvRowHead.getCell(j).getStringCellValue());
                XSSFCell lvCell = lvRowData.getCell(j);
                Object lvCellValue = getCellValue(lvCell);
                switch (lvHeaderKey) {
                    case "product_name":
                        productTemp.setProductName((String) lvCellValue);
                        break;
                    case "product_code":
                        break;
                    case "product_type_code":
                        String lvProductTypeCode = (String) lvCellValue;
                        Category lvProductType = categoryRepository.findByTypeAndCode(CATEGORY.PRODUCT_TYPE.name(), lvProductTypeCode);
                        if (lvProductType != null) {
                            productTemp.setProductTypeId(lvProductType.getId());
                        } else {
                            highlightCellInvalidValue(lvCell);
                            lvMessageError.append(",").append(lvCell.getAddress().formatAsString());
                        }
                        break;
                    case "brand_code":
                        String lvBrandCode = (String) lvCellValue;
                        Category lvBrand = categoryRepository.findByTypeAndCode(CATEGORY.BRAND.name(), lvBrandCode);
                        if (lvBrand != null) {
                            productTemp.setBrandId(lvBrand.getId());
                        } else {
                            highlightCellInvalidValue(lvCell);
                            lvMessageError.append(",").append(lvCell.getAddress().formatAsString());
                        }
                        break;
                    case "unit_code":
                        String lvUnitCode = (String) lvCellValue;
                        Category lvUnit = categoryRepository.findByTypeAndCode(CATEGORY.UNIT.name(), lvUnitCode);
                        if (lvUnit != null) {
                            productTemp.setUnitId(lvUnit.getId());
                        } else {
                            highlightCellInvalidValue(lvCell);
                            lvMessageError.append(",").append(lvCell.getAddress().formatAsString());
                        }
                        break;
                    case "color_code":
                        String lvColorCode = (String) lvCellValue;
                        Category lvColor = categoryRepository.findByTypeAndCode(CATEGORY.COLOR.name(), lvColorCode);
                        if (lvColor != null) {
                            productVariantTemp.setColorId(lvColor.getId());
                        } else {
                            highlightCellInvalidValue(lvCell);
                            lvMessageError.append(",").append(lvCell.getAddress().formatAsString());
                        }
                        break;
                    case "size_code":
                        String lvSizeCode = (String) lvCellValue;
                        Category lvSize = categoryRepository.findByTypeAndCode(CATEGORY.SIZE.name(), lvSizeCode);
                        if (lvSize != null) {
                            productVariantTemp.setSizeId(lvSize.getId());
                        } else {
                            highlightCellInvalidValue(lvCell);
                            lvMessageError.append(",").append(lvCell.getAddress().formatAsString());
                        }
                        break;
                    case "fabric_type_code":
                        String lvFabricTypeCode = (String) lvCellValue;
                        Category lvFabricType = categoryRepository.findByTypeAndCode(CATEGORY.FABRIC_TYPE.name(), lvFabricTypeCode);
                        if (lvFabricType != null) {
                            productVariantTemp.setFabricTypeId(lvFabricType.getId());
                        } else {
                            highlightCellInvalidValue(lvCell);
                            lvMessageError.append(",").append(lvCell.getAddress().formatAsString());
                        }
                        break;
                    case "storage_qty":
                        productVariantTemp.setStorageQty((int) lvCellValue);
                        break;
                    case "sold_qty":
                        productVariantTemp.setSoldQty((int) lvCellValue);
                        break;
                    case "defective_qty":
                        productVariantTemp.setDefectiveQty((int) lvCellValue);
                        break;
                    case "product_status":
                        productVariantTemp.setStatus((String) lvCellValue);
                        break;
                }
            }
        }

        if (!lvMessageError.toString().equals("")) {
            mvEximResult.setResultStatus("NOK");
            return;
        }

        //Storage into db
        //...
        productTempRepository.saveAll(lvData);

        setData(lvData);
    }

    @Override
    public void postImport(TemplateExport pTemplateExport, ImportHistory pImportInfoMdl) throws AppException {
        super.postImport(pTemplateExport, pImportInfoMdl);
    }
}