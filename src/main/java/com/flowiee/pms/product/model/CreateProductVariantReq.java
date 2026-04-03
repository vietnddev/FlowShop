package com.flowiee.pms.product.model;

import com.flowiee.pms.product.dto.ProductVariantDTO;
import lombok.Data;

import java.util.List;

@Data
public class CreateProductVariantReq {
    Long productId;
    List<ProductVariantDTO> variants;
}