package com.flowiee.pms.modules.inventory.model;

import com.flowiee.pms.modules.inventory.dto.ProductAttributeDTO;
import lombok.Data;

import java.util.List;

@Data
public class CreateProductAttributeReq {
    Long productId;
    List<ProductAttributeDTO> attributes;
}