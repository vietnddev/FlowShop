package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.inventory.entity.Material;

@Repository
public interface MaterialRepository extends BaseRepository<Material, Long> {
    @Query("from Material m " +
           "where 1=1 " +
           "and (:supplierId is null or m.supplier.id=:supplierId) " +
           "and (:unitId is null or m.unit.id=:unitId) " +
           "and (:code is null or m.code=:code) " +
           "and (:name is null or m.name like %:name%) " +
           "and (:location is null or m.location like %:location%) " +
           "and (:status is null or m.status=:status)")
    Page<Material> findAll(@Param("supplierId") Long supplierId,
                           @Param("unitId") Long unitId,
                           @Param("code") String code,
                           @Param("name") String name,
                           @Param("location") String location,
                           @Param("status") String status,
                           Pageable pageable);

    @Modifying
    @Query("update Material m set m.quantity = (m.quantity + :quantity) where m.id=:materialId")
    void updateQuantityIncrease(@Param("quantity") Integer quantity, @Param("materialId") long materialId);

    @Modifying
    @Query("update Material m set m.quantity = (m.quantity - :quantity) where m.id=:materialId")
    void updateQuantityDecrease(@Param("quantity") Integer quantity, @Param("materialId") long materialId);
}