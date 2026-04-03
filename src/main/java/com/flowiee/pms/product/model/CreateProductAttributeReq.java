package com.flowiee.pms.product.model;

import com.flowiee.pms.product.dto.ProductAttributeDTO;
import lombok.Data;

import java.util.List;

@Data
public class CreateProductAttributeReq {
    Long productId;
    List<ProductAttributeDTO> attributes;
}