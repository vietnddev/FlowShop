package com.flowiee.pms.promotion.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.promotion.dto.GiftCatalogDTO;

import java.util.List;

public interface GiftCatalogService extends ICurdService<GiftCatalogDTO> {
    List<GiftCatalogDTO> getActiveGifts();
}