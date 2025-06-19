package com.flowiee.pms.modules.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.inventory.entity.MaterialTemp;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MaterialTempRepository extends JpaRepository<MaterialTemp, Long> {
    @Query("from MaterialTemp m where m.ticketImport.id=:importId")
    List<MaterialTemp> findByImportId(@Param("importId") Long importId);
    
    @Query("from MaterialTemp m where m.ticketImport.id=:importId and m.material.id=:materialId")
    MaterialTemp findMaterialInGoodsImport(@Param("importId") Long importId, @Param("materialId") Long materialId);

    @Transactional
    @Modifying
    @Query("update MaterialTemp m set m.quantity = (m.quantity + :quantity) where m.id =:materialTempId")
    void updateQuantityIncrease(@Param("materialTempId") Long materialTempId, @Param("quantity") Integer quantity);

    @Transactional
    @Modifying
    @Query("update MaterialTemp m set m.storageQty=:storageQty where m.id =:materialTempId")
    void updateStorageQuantity(@Param("materialTempId") Long materialTempId, @Param("storageQty") Integer storageQty);
}