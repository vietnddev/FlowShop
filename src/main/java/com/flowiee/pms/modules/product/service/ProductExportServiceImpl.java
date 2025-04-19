package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.enumeration.CATEGORY;
import com.flowiee.pms.common.enumeration.ProductEximKeyField;
import com.flowiee.pms.common.enumeration.ProductStatus;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.modules.category.entity.Category;
import com.flowiee.pms.modules.product.entity.Product;
import com.flowiee.pms.modules.product.entity.ProductDetail;
import com.flowiee.pms.modules.product.entity.ProductPrice;
import com.flowiee.pms.common.base.service.BaseExportService;
import com.flowiee.pms.modules.product.dto.ProductVariantDTO;
import com.flowiee.pms.modules.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductExportServiceImpl extends BaseExportService {
    private final CategoryService mvCategoryService;

    private final List<CATEGORY> mvProductCategories = List.of(CATEGORY.PRODUCT_TYPE, CATEGORY.BRAND, CATEGORY.UNIT, CATEGORY.COLOR, CATEGORY.SIZE, CATEGORY.FABRIC_TYPE);
    private Map<CATEGORY, List<Category>> mvCategoryMap;
    private List<ProductDetail> mvDataExport;

    @Override
    protected void prepareData(Object pCondition, boolean pTemplateOnly) {
        ProductVariantDTO lvCondition = pCondition != null ? (ProductVariantDTO) pCondition : new ProductVariantDTO();
        if (pTemplateOnly) {
            //Sample data
            mvDataExport = List.of(ProductDetail.builder()
                    .variantName("Áo thun form boxy thêu hình Xích đu trái tim")
                    .variantCode("JNATH069")
                    .storageQty(5)
                    .soldQty(1)
                    .defectiveQty(0)
                    .product(Product.builder()
                            .brand(new Category(null, "brand..."))
                            .productType(new Category(null, "product type..."))
                            .unit(new Category(null, "unit..."))
                            .build())
                    .color(new Category(null, "color..."))
                    .size(new Category(null, "size..."))
                    .fabricType(new Category(null, "fabric type..."))
                    .priceList(List.of(ProductPrice.builder()
                            .retailPrice(BigDecimal.valueOf(200000))
                            .wholesalePrice(BigDecimal.valueOf(180000))
                            .purchasePrice(BigDecimal.ZERO)
                            .costPrice(BigDecimal.ZERO)
                            .build()))
                    .status(ProductStatus.ACT)
                    .build());
        } else {
            CriteriaBuilder lvCriteriaBuilder = mvEntityManager.getCriteriaBuilder();
            CriteriaQuery<ProductDetail> lvCriteriaQuery = lvCriteriaBuilder.createQuery(ProductDetail.class);
            Root<ProductDetail> lvRoot = lvCriteriaQuery.from(ProductDetail.class);

            lvRoot.fetch("product", JoinType.LEFT);
            lvRoot.fetch("priceList", JoinType.LEFT);

            List<Predicate> lvPredicates = new ArrayList<>();
            addEqualCondition(lvCriteriaBuilder, lvPredicates, lvRoot.get("product").get("productType").get("id"), lvCondition.getProductTypeId());
            addEqualCondition(lvCriteriaBuilder, lvPredicates, lvRoot.get("product").get("brand").get("id"), lvCondition.getBrandId());
            addEqualCondition(lvCriteriaBuilder, lvPredicates, lvRoot.get("color").get("id"), lvCondition.getColorId());
            addEqualCondition(lvCriteriaBuilder, lvPredicates, lvRoot.get("size").get("id"), lvCondition.getSizeId());
            addEqualCondition(lvCriteriaBuilder, lvPredicates, lvRoot.get("fabricType").get("id"), lvCondition.getFabricTypeId());

            TypedQuery<ProductDetail> lvTypedQuery = initCriteriaQuery(lvCriteriaBuilder, lvCriteriaQuery, lvRoot, lvPredicates, Pageable.unpaged());

            mvDataExport = lvTypedQuery.getResultList();
        }

        mvDataExportSize = mvDataExport.size();
        mvCategoryMap = mvCategoryService.findByType(mvProductCategories);
        mvHeadKeyLine = 1;
        mvDataBeginLine = 3;
    }

    @Override
    protected void writeData(Object pCondition) {
        List<ProductDetail> lvDataExportList = mvDataExport;

        XSSFRow lvRowHead = getRowHeadKey();

        for (int i = 0; i < mvDataExportSize; i++) {
            ProductDetail lvProductVariant = lvDataExportList.get(i);
            Product lvProduct = lvProductVariant.getProduct();
            ProductPrice lvProductPrice = lvProductVariant.getVariantPrice();

            XSSFRow lvRow = mvDataSheet.createRow(i + mvDataBeginLine);

            for (int j = 0; j < lvRowHead.getPhysicalNumberOfCells(); j++) {
                ProductEximKeyField lvHeadKey = ProductEximKeyField.valueOf(CoreUtils.trim(lvRowHead.getCell(j).getStringCellValue()));
                XSSFCell lvCell = lvRow.createCell(j);

                switch (lvHeadKey) {
                    case product_name ->
                            lvCell.setCellValue(lvProductVariant.getVariantName());
                    case product_code ->
                            lvCell.setCellValue(lvProductVariant.getVariantCode());
                    case product_type_code ->
                            lvCell.setCellValue(lvProduct.getProductType().getName());
                    case brand_code ->
                            lvCell.setCellValue(lvProduct.getBrand().getName());
                    case unit_code ->
                            lvCell.setCellValue(lvProduct.getUnit().getName());
                    case color_code ->
                            lvCell.setCellValue(lvProductVariant.getColor().getName());
                    case size_code ->
                            lvCell.setCellValue(lvProductVariant.getSize().getName());
                    case fabric_type_code ->
                            lvCell.setCellValue(lvProductVariant.getFabricType().getName());
                    case storage_qty ->
                            lvCell.setCellValue(lvProductVariant.getStorageQty());
                    case sold_qty ->
                            lvCell.setCellValue(lvProductVariant.getSoldQty());
                    case defective_qty ->
                            lvCell.setCellValue(lvProductVariant.getDefectiveQty());
                    case retail_price ->
                            lvCell.setCellValue(CoreUtils.coalesce(lvProductPrice.getRetailPrice()).toPlainString());
                    case wholesale_price ->
                            lvCell.setCellValue(CoreUtils.coalesce(lvProductPrice.getWholesalePrice()).toPlainString());
                    case purchase_price ->
                            lvCell.setCellValue(CoreUtils.coalesce(lvProductPrice.getPurchasePrice()).toPlainString());
                    case cost_price ->
                            lvCell.setCellValue(CoreUtils.coalesce(lvProductPrice.getCostPrice()).toPlainString());
                    case product_status ->
                            lvCell.setCellValue(lvProductVariant.getStatus().getLabel());
                    default ->
                            throw new IllegalStateException("Unexpected value: " + lvHeadKey);
                }
            }
        }
        createDropdownList();
    }

    private void createDropdownList() {
        createDropdownList(getListName(mvCategoryMap.get(CATEGORY.PRODUCT_TYPE)), ProductEximKeyField.product_type_code.name());
        createDropdownList(getListName(mvCategoryMap.get(CATEGORY.BRAND)), ProductEximKeyField.brand_code.name());
        createDropdownList(getListName(mvCategoryMap.get(CATEGORY.UNIT)), ProductEximKeyField.unit_code.name());
        createDropdownList(getListName(mvCategoryMap.get(CATEGORY.COLOR)), ProductEximKeyField.color_code.name());
        createDropdownList(getListName(mvCategoryMap.get(CATEGORY.SIZE)), ProductEximKeyField.size_code.name());
        createDropdownList(getListName(mvCategoryMap.get(CATEGORY.FABRIC_TYPE)), ProductEximKeyField.fabric_type_code.name());
        createDropdownList(Arrays.stream(ProductStatus.values()).map(ProductStatus::getLabel).toList(), ProductEximKeyField.product_status.name());
    }

    private List<String> getListName(List<Category> pCategoryList) {
        if (CollectionUtils.isEmpty(pCategoryList)) {
            return List.of();
        }
        return pCategoryList.stream().map(Category::getName).toList();
    }
}