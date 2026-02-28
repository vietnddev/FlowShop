package com.flowiee.pms.modules.inventory.model;

import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import lombok.Data;

import java.util.List;

@Data
public class CreateProductVariantReq {
    Long productId;
    List<ProductVariantDTO> variants;
}