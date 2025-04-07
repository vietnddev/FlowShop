package com.flowiee.pms.service.product;

import com.flowiee.pms.entity.product.ProductDescription;

public interface ProductDescriptionService {
    ProductDescription findByProductId(Long pProductId);
}