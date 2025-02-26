package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.base.service.BaseImportService;
import com.flowiee.pms.common.enumeration.CATEGORY;
import com.flowiee.pms.common.enumeration.PID;
import com.flowiee.pms.common.enumeration.ProductEximKeyField;
import com.flowiee.pms.common.enumeration.ProductStatus;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.entity.product.Product;
import com.flowiee.pms.entity.product.ProductTemp;
import com.flowiee.pms.entity.product.ProductVariantTemp;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.model.dto.ProductDTO;
import com.flowiee.pms.model.dto.ProductVariantDTO;
import com.flowiee.pms.repository.category.CategoryRepository;
import com.flowiee.pms.repository.product.ProductTempRepository;
import com.flowiee.pms.repository.product.ProductVariantTempRepository;
import com.flowiee.pms.security.UserSession;
import com.flowiee.pms.service.product.ProductInfoService;
import com.flowiee.pms.service.product.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ProductImportServiceImpl extends BaseImportService {
    private final ProductVariantTempRepository productVariantTempRepository;
    private final ProductTempRepository productTempRepository;
    private final ProductVariantService productVariantService;
    private final ProductInfoService productService;
    private final CategoryRepository categoryRepository;
    private final UserSession userSession;

    @Override
    protected void writeData() throws AppException, IOException {
        List<ProductTemp> lvData = new ArrayList<>();

        StringBuilder lvMessageError = new StringBuilder();

        Map<String, ProductTemp> productMap = new HashMap<>();

        XSSFRow lvRowHead = getRowHeadKey();
        for (int i = mvDataBeginLine; i < mvDataSheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow lvRowData = mvDataSheet.getRow(i);
            if (lvRowData == null) {
                continue;
            }

            ProductTemp lvProductTemp = new ProductTemp();
            ProductVariantTemp lvVariantTemp = new ProductVariantTemp();

            String productName = null;
            for (int j = 0; j < lvRowHead.getPhysicalNumberOfCells(); j++) {
                XSSFCell lvCellHead = lvRowHead.getCell(j);
                if (lvCellHead == null) {
                    continue;
                }
                XSSFCell lvCell = lvRowData.getCell(j);
                ProductEximKeyField lvHeadKey = ProductEximKeyField.valueOf(CoreUtils.trim(lvCellHead.getStringCellValue()));
                Object cellValue = getCellValue(lvCell);

                switch (lvHeadKey) {
                    case product_name:
                        productName = (String) cellValue;
                        lvProductTemp.setProductName(productName);
                        break;
                    case product_type_code:
                        Category lvProductType = categoryRepository.findByTypeAndName(CATEGORY.PRODUCT_TYPE.name(), cellValue.toString());
                        lvProductTemp.setProductTypeId(lvProductType.getId());
                        break;
                    case brand_code:
                        Category lvBrand = categoryRepository.findByTypeAndName(CATEGORY.BRAND.name(), cellValue.toString());
                        lvProductTemp.setBrandId(lvBrand.getId());
                        break;
                    case unit_code:
                        Category lvUnit = categoryRepository.findByTypeAndName(CATEGORY.UNIT.name(), cellValue.toString());
                        lvProductTemp.setUnitId(lvUnit.getId());
                        break;
                    case product_code:
                        lvVariantTemp.setSku((String) cellValue);
                        break;
                    case color_code:
                        Category lvColor = categoryRepository.findByTypeAndName(CATEGORY.COLOR.name(), cellValue.toString());
                        lvVariantTemp.setColorId(lvColor.getId());
                        break;
                    case size_code:
                        Category lvSize = categoryRepository.findByTypeAndName(CATEGORY.SIZE.name(), cellValue.toString());
                        lvVariantTemp.setSizeId(lvSize.getId());
                        break;
                    case fabric_type_code:
                        Category lvFabricType = categoryRepository.findByTypeAndName(CATEGORY.FABRIC_TYPE.name(), cellValue.toString());
                        lvVariantTemp.setFabricTypeId(lvFabricType.getId());
                        break;
                    case storage_qty:
                        lvVariantTemp.setStorageQty(parseInt(cellValue));
                        break;
                    case sold_qty:
                        lvVariantTemp.setSoldQty(parseInt(cellValue));
                        break;
                    case defective_qty:
                        lvVariantTemp.setDefectiveQty(parseInt(cellValue));
                        break;
                    default:
                        break;
                }
            }

            if (productName == null || productName.isEmpty()) {
                continue;
            }

            // Kiểm tra nếu sản phẩm đã tồn tại, thêm biến thể vào danh sách
            ProductTemp existingProduct = productMap.get(productName);
            if (existingProduct != null) {
                existingProduct.getProductVariantTempList().add(lvVariantTemp);
            } else {
                lvProductTemp.setProductVariantTempList(new ArrayList<>());
                lvProductTemp.getProductVariantTempList().add(lvVariantTemp);
                productMap.put(productName, lvProductTemp);
            }
        }
        lvData.addAll(productMap.values());

        productTempRepository.deleteAll();
        for (ProductTemp p : lvData) {
            p.setCreatedAt(LocalDateTime.now());
            p.setCreatedBy(userSession.getUserPrincipal().getUsername());
            ProductTemp productTempSaved = productTempRepository.save(p);
            for (ProductVariantTemp pv : p.getProductVariantTempList()) {
                pv.setCreatedAt(LocalDateTime.now());
                pv.setCreatedBy(userSession.getUserPrincipal().getUsername());
                pv.setProductTemp(productTempSaved);
                productVariantTempRepository.save(pv);
            }
        }

        setData(lvData);

        if (!lvMessageError.toString().equals("")) {
            mvEximResult.setResultStatus(lvMessageError.substring(0, 1));
            return;
        }
    }

    private Integer parseInt(Object obj) {
        if (obj == null) return null;
        try {
            double doubleVal = Double.parseDouble(String.valueOf(obj));
            return BigDecimal.valueOf(doubleVal).setScale(0).intValue();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public String approveData() {
        AtomicInteger totalRecordApproved = new AtomicInteger();
        productTempRepository.findAll().forEach(p -> {
            ProductDTO lvPDto = new ProductDTO();
            lvPDto.setPID(PID.CLOTHES.name());
            lvPDto.setProductName(p.getProductName());
            lvPDto.setBrandId(p.getBrandId());
            lvPDto.setProductTypeId(p.getProductTypeId());
            lvPDto.setUnitId(p.getUnitId());
            Product productSaved = productService.save(lvPDto);

            p.getProductVariantTempList().forEach(pv -> {
                ProductVariantDTO lvPvDto = new ProductVariantDTO();
                lvPvDto.setProduct(productSaved);
                pv.setColorId(pv.getColorId());
                pv.setSizeId(pv.getSizeId());
                pv.setFabricTypeId(pv.getFabricTypeId());
                pv.setStorageQty(pv.getStorageQty());
                pv.setSoldQty(pv.getSoldQty());
                pv.setDefectiveQty(pv.getDefectiveQty());
                pv.setStatus(ProductStatus.ACT.name());
                productVariantService.save(lvPvDto);
            });

            totalRecordApproved.getAndIncrement();
        });

        logger.info("Approved {} record (s)", totalRecordApproved.get());

        return "Data is approved OK";
    }
}