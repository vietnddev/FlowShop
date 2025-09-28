package com.flowiee.pms.modules.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.inventory.entity.MaterialTemp;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MaterialTempRepository extends JpaRepository<MaterialTemp, Long> {
    @Transactional
    @Modifying
    @Query("update MaterialTemp m set m.quantity = (m.quantity + :quantity) where m.id =:materialTempId")
    void updateQuantityIncrease(@Param("materialTempId") Long materialTempId, @Param("quantity") Integer quantity);
}