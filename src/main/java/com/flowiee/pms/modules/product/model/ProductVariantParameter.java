package com.flowiee.pms.modules.product.model;

import com.flowiee.pms.common.model.BaseParameter;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductVariantParameter extends BaseParameter {
    private String txtSerch;
    private Long productId;
    private Long ticketImportId;
    private Long brandId;
    private Long colorId;
    private Long sizeId;
    private Long fabricTypeId;
    private Boolean availableForSales;
    private Boolean checkInAnyCart = false;
}