package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.inventory.entity.MaterialHistory;

import java.util.List;

@Repository
public interface MaterialHistoryRepository extends BaseRepository<MaterialHistory, Long> {
    @Query("from MaterialHistory m where m.material.id=:materialId")
    List<MaterialHistory> findByMaterialId(@Param("materialId") Long materialId);

    @Query("from MaterialHistory m where m.fieldName=:fieldName")
    List<MaterialHistory> findByFieldName(@Param("fieldName") String fieldName);
}