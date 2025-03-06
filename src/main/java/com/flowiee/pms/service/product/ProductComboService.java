package com.flowiee.pms.service.product;

import com.flowiee.pms.entity.product.ProductCombo;
import com.flowiee.pms.base.BaseCurdService;
import org.springframework.data.domain.Page;

public interface ProductComboService extends BaseCurdService<ProductCombo> {
    Page<ProductCombo> findAll(int pageSize, int pageNum);
}