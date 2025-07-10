package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.modules.inventory.dto.ProductDamagedDTO;

import java.util.List;

public interface ProductDamagedService extends ICurdService<ProductDamagedDTO> {
    List<ProductDamagedDTO> find();
}