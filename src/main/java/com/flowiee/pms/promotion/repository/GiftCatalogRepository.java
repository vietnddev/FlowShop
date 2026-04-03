package com.flowiee.pms.promotion.repository;

import com.flowiee.pms.shared.base.BaseRepository;
import com.flowiee.pms.promotion.dto.GiftCatalog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftCatalogRepository extends BaseRepository<GiftCatalog, Long> {
    List<GiftCatalog> findByIsActiveTrue();
}