package com.flowiee.pms.modules.sales.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.sales.entity.GarmentFactory;

@Repository
public interface GarmentFactoryRepository extends BaseRepository<GarmentFactory, Long> {
}