package com.flowiee.pms.modules.inventory.model;

import com.flowiee.pms.common.model.BaseParameter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ProductSearchRequest {
    private String productCategory;
    private String txtSearch;
    private Long brandId;
    private Long productTypeId;
    private Long colorId;
    private Long sizeId;
    private Long unitId;
    private String gender;
    private Boolean isSaleOff;
    private Boolean isHotTrend;
    private String status;
    private int pageNum = -1;
    private int pageSize = -1;

    public static ProductSearchRequest of(int pageNum, int pageSize) {
        ProductSearchRequest lvRequest = ProductSearchRequest.builder().build();
        lvRequest.setPageNum(pageNum);
        lvRequest.setPageSize(pageSize);
        return lvRequest;
    }
}