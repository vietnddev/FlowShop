package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.shared.base.BaseDTO;
import com.flowiee.pms.modules.inventory.entity.ProductHistory;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class ProductHistoryDTO extends BaseDTO implements Serializable {
    private ProductDTO product;
    private ProductVariantDTO productDetail;
    private ProductAttributeDTO productAttribute;
    private String title;
    private String field;
    private String oldValue;
    private String newValue;
    private Long productId;
    private Long productVariantId;
    private Long productAttributeId;

    public static ProductHistoryDTO toDto (ProductHistory pInput) {
        return ProductHistoryDTO.builder()
                .product(null)
                .productDetail(null)
                .productAttribute(null)
                .title(pInput.getTitle())
                .field(pInput.getField())
                .oldValue(pInput.getOldValue())
                .newValue(pInput.getNewValue())
                .productId(null)
                .productVariantId(null)
                .productAttributeId(null)
                .build();
    }
}