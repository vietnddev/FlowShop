package com.flowiee.pms.modules.product.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSummaryInfoModel {
    private Long productId;
    private Long colorId;
    private String colorName;
    private Long sizeId;
    private String sizeName;
    private Long quantity;

    public ProductSummaryInfoModel(Long productId, Long colorId, String colorName, Long sizeId, String sizeName, Long quantity) {
        this.productId = productId;
        this.colorId = colorId;
        this.colorName = colorName;
        this.sizeId = sizeId;
        this.sizeName = sizeName;
        this.quantity = quantity;
    }
}