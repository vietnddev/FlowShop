package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.common.enumeration.CATEGORY;
import com.flowiee.pms.common.enumeration.ProductStatus;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.entity.product.Product;
import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.entity.product.ProductPrice;
import com.flowiee.pms.base.service.BaseExportService;
import com.flowiee.pms.model.dto.ProductVariantDTO;
import com.flowiee.pms.service.category.CategoryService;
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
            ProductDetail productVariant = lvDataExportList.get(i);
            ProductPrice lvProductPrice = productVariant.getVariantPrice();
            if (lvProductPrice == null) {
                lvProductPrice = new ProductPrice();
            }

            XSSFRow lvRow = mvDataSheet.createRow(i + mvDataBeginLine);

            for (int j = 0; j < lvRowHead.getPhysicalNumberOfCells(); j++) {
                String lvHeadKey = lvRowHead.getCell(j).getStringCellValue();
                XSSFCell lvCell = lvRow.createCell(j);

                switch (lvHeadKey) {
                    case "product_name"      -> lvCell.setCellValue(productVariant.getVariantName());
                    case "product_code"      -> lvCell.setCellValue(productVariant.getVariantCode());
                    case "product_type_code" -> lvCell.setCellValue(productVariant.getProduct().getProductType().getName());
                    case "brand_code"        -> lvCell.setCellValue(productVariant.getProduct().getBrand().getName());
                    case "unit_code"         -> lvCell.setCellValue(productVariant.getProduct().getUnit().getName());
                    case "color_code"        -> lvCell.setCellValue(productVariant.getColor().getName());
                    case "size_code"         -> lvCell.setCellValue(productVariant.getSize().getName());
                    case "fabric_type_code"  -> lvCell.setCellValue(productVariant.getFabricType().getName());
                    case "storage_qty"       -> lvCell.setCellValue(productVariant.getStorageQty());
                    case "sold_qty"          -> lvCell.setCellValue(productVariant.getSoldQty());
                    case "defective_qty"     -> lvCell.setCellValue(productVariant.getDefectiveQty());
                    case "retail_price"      -> lvCell.setCellValue(CoreUtils.coalesce(lvProductPrice.getRetailPrice()).toPlainString());
                    case "wholesale_price"   -> lvCell.setCellValue(CoreUtils.coalesce(lvProductPrice.getWholesalePrice()).toPlainString());
                    case "purchase_price"    -> lvCell.setCellValue(CoreUtils.coalesce(lvProductPrice.getPurchasePrice()).toPlainString());
                    case "cost_price"        -> lvCell.setCellValue(CoreUtils.coalesce(lvProductPrice.getCostPrice()).toPlainString());
                    case "product_status"    -> lvCell.setCellValue(productVariant.getStatus().getLabel());
                    default -> throw new IllegalStateException("Unexpected value: " + lvHeadKey);
                }
            }
        }
        createDropdownList();
    }

    private void createDropdownList() {
        createDropdownList(getListName(mvCategoryMap.get(CATEGORY.PRODUCT_TYPE)), "product_type_code");
        createDropdownList(getListName(mvCategoryMap.get(CATEGORY.BRAND)), "brand_code");
        createDropdownList(getListName(mvCategoryMap.get(CATEGORY.UNIT)), "unit_code");
        createDropdownList(getListName(mvCategoryMap.get(CATEGORY.COLOR)), "color_code");
        createDropdownList(getListName(mvCategoryMap.get(CATEGORY.SIZE)), "size_code");
        createDropdownList(getListName(mvCategoryMap.get(CATEGORY.FABRIC_TYPE)), "fabric_type_code");
        createDropdownList(Arrays.stream(ProductStatus.values()).map(ProductStatus::getLabel).toList(), "product_status");
    }

    private List<String> getListName(List<Category> pCategoryList) {
        if (CollectionUtils.isEmpty(pCategoryList)) {
            return List.of();
        }
        return pCategoryList.stream().map(Category::getName).toList();
    }
}