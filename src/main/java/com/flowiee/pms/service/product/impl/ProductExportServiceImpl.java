package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.model.dto.ProductVariantDTO;
import com.flowiee.pms.base.service.BaseExportService;
import com.flowiee.pms.service.product.ProductVariantService;
import com.flowiee.pms.common.utils.CommonUtils;
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
public class ProductExportServiceImpl extends BaseExportService {
    ProductVariantService mvProductVariantService;

    @Override
    protected void writeData(Object pCondition) {
        XSSFSheet sheet = mvWorkbook.getSheetAt(0);
        List<ProductVariantDTO> listDataVariant = mvProductVariantService.findAll();
        for (int i = 0; i < listDataVariant.size(); i++) {
            ProductVariantDTO productVariant = listDataVariant.get(i);
            XSSFRow row = sheet.createRow(i + 3);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(productVariant.getProductTypeName());
            row.createCell(2).setCellValue(productVariant.getVariantName());
            row.createCell(3).setCellValue(productVariant.getSizeName());
            row.createCell(4).setCellValue(productVariant.getColorName());
            row.createCell(5).setCellValue(productVariant.getFabricTypeName());
            row.createCell(6).setCellValue(CommonUtils.formatToVND(productVariant.getOriginalPrice()));
            row.createCell(7).setCellValue(CommonUtils.formatToVND(productVariant.getDiscountPrice()));
            row.createCell(8).setCellValue(productVariant.getStorageQty());
            row.createCell(9).setCellValue(productVariant.getSoldQty());
            row.createCell(10).setCellValue(productVariant.getStatus().getLabel());

            setBorderCell(row, 0, 10);
        }
    }
}