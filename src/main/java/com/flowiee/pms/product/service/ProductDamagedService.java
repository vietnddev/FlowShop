package com.flowiee.pms.product.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.product.dto.ProductDamagedDTO;

import java.util.List;

public interface ProductDamagedService extends ICurdService<ProductDamagedDTO> {
    List<ProductDamagedDTO> find();
}