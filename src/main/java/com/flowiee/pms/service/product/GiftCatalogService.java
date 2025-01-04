package com.flowiee.pms.service.product;

import com.flowiee.pms.entity.product.GiftCatalog;
import com.flowiee.pms.base.service.BaseCurdService;

import java.util.List;

public interface GiftCatalogService extends BaseCurdService<GiftCatalog> {
    List<GiftCatalog> getActiveGifts();
}