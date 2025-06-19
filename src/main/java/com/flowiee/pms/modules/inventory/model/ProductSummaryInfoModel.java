package com.flowiee.pms.modules.inventory.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSummaryInfoModel {
    private Long id;
    private String variantCode;
    private String variantName;
    private Long productId;
    private Long fabricTypeId;
    private String fabricTypeName;
    private Long colorId;
    private String colorName;
    private Long sizeId;
    private String sizeName;
    private Long quantity;
    private Long soldQty;

    public ProductSummaryInfoModel(Long id, String variantCode, String variantName, Long productId,
                                   Long fabricTypeId, String fabricTypeName, Long colorId, String colorName, Long sizeId, String sizeName,
                                   Long quantity, Long soldQty) {
        this.id = id;
        this.variantCode = variantCode;
        this.variantName = variantName;
        this.productId = productId;
        this.fabricTypeId = fabricTypeId;
        this.fabricTypeName = fabricTypeName;
        this.colorId = colorId;
        this.colorName = colorName;
        this.sizeId = sizeId;
        this.sizeName = sizeName;
        this.quantity = quantity;
        this.soldQty = soldQty;
    }
}