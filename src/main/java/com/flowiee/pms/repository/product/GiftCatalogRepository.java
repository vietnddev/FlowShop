package com.flowiee.pms.repository.product;

import com.flowiee.pms.base.BaseRepository;
import com.flowiee.pms.entity.product.GiftCatalog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftCatalogRepository extends BaseRepository<GiftCatalog, Long> {
    List<GiftCatalog> findByIsActiveTrue();
}