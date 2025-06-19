package com.flowiee.pms.modules.inventory.model;

import com.flowiee.pms.common.model.BaseParameter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ProductVariantSearchRequest {
    private String txtSearch;
    private Long productId;
    private Long ticketImportId;
    private Long brandId;
    private Long colorId;
    private Long sizeId;
    private Long fabricTypeId;
    private Boolean availableForSales;
    private Boolean checkInAnyCart = false;
    private int pageNum = -1;
    private int pageSize = -1;

    public static ProductVariantSearchRequest of(int pageNum, int pageSize) {
        ProductVariantSearchRequest lvRequest = ProductVariantSearchRequest.builder().build();
        lvRequest.setPageNum(pageNum);
        lvRequest.setPageSize(pageSize);
        return lvRequest;
    }
}