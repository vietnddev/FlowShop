package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.inventory.entity.GiftCatalog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftCatalogRepository extends BaseRepository<GiftCatalog, Long> {
    List<GiftCatalog> findByIsActiveTrue();
}