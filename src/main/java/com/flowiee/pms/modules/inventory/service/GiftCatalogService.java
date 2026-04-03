package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.modules.inventory.dto.GiftCatalogDTO;

import java.util.List;

public interface GiftCatalogService extends ICurdService<GiftCatalogDTO> {
    List<GiftCatalogDTO> getActiveGifts();
}