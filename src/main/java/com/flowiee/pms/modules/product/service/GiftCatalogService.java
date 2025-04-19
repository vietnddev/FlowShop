package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.product.dto.GiftCatalogDTO;

import java.util.List;

public interface GiftCatalogService extends BaseCurdService<GiftCatalogDTO> {
    List<GiftCatalogDTO> getActiveGifts();
}