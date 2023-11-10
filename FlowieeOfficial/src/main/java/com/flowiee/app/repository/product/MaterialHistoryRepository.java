package com.flowiee.app.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.flowiee.app.entity.MaterialHistory;

import java.util.List;

@Repository
public interface MaterialHistoryRepository extends JpaRepository<MaterialHistory, Integer> {
    @Query("from MaterialHistory m where m.material.id=:materialId")
    List<MaterialHistory> findByMaterialId(Integer materialId);

    @Query("from MaterialHistory m where m.action=:action")
    List<MaterialHistory> findByAction(String action);
}