package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.base.service.BaseImportService;
import com.flowiee.pms.common.enumeration.CATEGORY;
import com.flowiee.pms.common.enumeration.PID;
import com.flowiee.pms.common.enumeration.ProductEximKeyField;
import com.flowiee.pms.common.enumeration.ProductStatus;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.modules.inventory.service.ProductInfoService;
import com.flowiee.pms.modules.inventory.service.ProductVariantService;
import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.modules.inventory.entity.ProductTemp;
import com.flowiee.pms.modules.inventory.entity.ProductVariantTemp;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.modules.inventory.dto.ProductDTO;
import com.flowiee.pms.modules.inventory.dto.ProductPriceDTO;
import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import com.flowiee.pms.modules.system.repository.CategoryRepository;
import com.flowiee.pms.modules.inventory.repository.ProductTempRepository;
import com.flowiee.pms.modules.inventory.repository.ProductVariantTempRepository;
import com.flowiee.pms.common.security.UserSession;
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
                    case retail_price:
                        lvVariantTemp.setRetailPrice(parseBigDecimal(cellValue));
                        break;
                    case wholesale_price:
                        lvVariantTemp.setWholesalePrice(parseBigDecimal(cellValue));
                        break;
                    case purchase_price:
                        lvVariantTemp.setPurchasePrice(parseBigDecimal(cellValue));
                        break;
                    case cost_price:
                        lvVariantTemp.setCostPrice(parseBigDecimal(cellValue));
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

        setImportStatus(String.format("OK, %s record (s) is pending for approval", lvData.size()));
    }

    private Integer parseInt(Object obj) {
        if (obj == null) return null;
        try {
            return parseBigDecimal(obj).setScale(0).intValue();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(Object obj) {
        if (obj == null) return null;
        try {
            double doubleVal = Double.parseDouble(String.valueOf(obj));
            return BigDecimal.valueOf(doubleVal);
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
            lvPDto.setProductCategory("-");
            lvPDto.setProductName(p.getProductName());
            lvPDto.setBrandId(p.getBrandId());
            lvPDto.setProductTypeId(p.getProductTypeId());
            lvPDto.setUnitId(p.getUnitId());
            ProductDTO productSaved = productService.save(lvPDto);

            p.getProductVariantTempList().forEach(pv -> {
                ProductPriceDTO lvPriceDto = new ProductPriceDTO();
                lvPriceDto.setRetailPrice(pv.getRetailPrice());
                lvPriceDto.setWholesalePrice(pv.getWholesalePrice());
                lvPriceDto.setPurchasePrice(pv.getPurchasePrice());
                lvPriceDto.setCostPrice(pv.getCostPrice());

                ProductVariantDTO lvPvDto = new ProductVariantDTO();
                lvPvDto.setProduct(new ProductDTO(productSaved.getId()));
                lvPvDto.setColorId(pv.getColorId());
                lvPvDto.setSizeId(pv.getSizeId());
                lvPvDto.setFabricTypeId(pv.getFabricTypeId());
                lvPvDto.setStorageQty(pv.getStorageQty());
                lvPvDto.setSoldQty(pv.getSoldQty());
                lvPvDto.setDefectiveQty(pv.getDefectiveQty());
                lvPvDto.setStatus(ProductStatus.ACT);
                lvPvDto.setPrice(lvPriceDto);
                productVariantService.save(lvPvDto);
            });

            totalRecordApproved.getAndIncrement();
        });

        if (totalRecordApproved.get() == 0) {
            return "No data is pending for approval";
        }

        productTempRepository.deleteAll();

        logger.info("Approved {} record (s)", totalRecordApproved.get());

        return String.format("%s record (s) is approved OK", totalRecordApproved.get());
    }
}