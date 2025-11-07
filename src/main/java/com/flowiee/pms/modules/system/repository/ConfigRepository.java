package com.flowiee.pms.modules.system.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.system.entity.SystemConfig;

import java.util.List;

@Repository
public interface ConfigRepository extends BaseRepository<SystemConfig, Long> {
    @Query("from SystemConfig order by code")
    List<SystemConfig> findAll();

    SystemConfig findByCode(String code);

    @Query("from SystemConfig where code in :listCode")
    List<SystemConfig> findByCode(@Param("listCode") List<String> listCode);
}