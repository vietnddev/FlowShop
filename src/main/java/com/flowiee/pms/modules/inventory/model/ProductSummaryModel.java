package com.flowiee.pms.modules.inventory.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSummaryModel {
    private long productId;
    private int stockQty;
    private int soldQty;
    private int defectiveQty;

    public ProductSummaryModel(long productId, int stockQty, int soldQty, int defectiveQty) {
        this.productId = productId;
        this.stockQty = stockQty;
        this.soldQty = soldQty;
        this.defectiveQty = defectiveQty;
    }
}