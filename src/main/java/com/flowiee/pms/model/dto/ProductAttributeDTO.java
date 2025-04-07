package com.flowiee.pms.model.dto;

import com.flowiee.pms.base.service.BaseDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProductAttributeDTO extends BaseDTO implements Serializable {
    private ProductVariantDTO productDetail;
    private String attributeName;
    private String attributeValue;
    private int sort;
    private boolean status;
    private List<ProductHistoryDTO> listProductHistory;
}