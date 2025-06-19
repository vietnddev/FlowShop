package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.modules.inventory.entity.ProductComboApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductComboApplyRepository extends JpaRepository<ProductComboApply, Long> {
    @Query("from ProductComboApply p where p.comboId = :comboId")
    List<ProductComboApply> findByComboId(@Param("comboId") Long comboId);
}