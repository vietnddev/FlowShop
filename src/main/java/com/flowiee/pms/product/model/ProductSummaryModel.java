package com.flowiee.pms.product.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSummaryModel {
    private long productId;
    private int stockQty;
    private int soldQty;
    private int defectiveQty;
    private int reservedQty;
    private boolean isActive;

    public ProductSummaryModel(long productId, int stockQty, int soldQty, int defectiveQty, int reservedQty, boolean isActive) {
        this.productId = productId;
        this.stockQty = stockQty;
        this.soldQty = soldQty;
        this.defectiveQty = defectiveQty;
        this.reservedQty = reservedQty;
        this.isActive = isActive;
    }
}