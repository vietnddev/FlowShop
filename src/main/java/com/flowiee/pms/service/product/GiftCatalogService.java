package com.flowiee.pms.service.product;

import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.model.dto.GiftCatalogDTO;

import java.util.List;

public interface GiftCatalogService extends BaseCurdService<GiftCatalogDTO> {
    List<GiftCatalogDTO> getActiveGifts();
}