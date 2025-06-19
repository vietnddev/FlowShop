package com.flowiee.pms.modules.sales.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.sales.entity.Supplier;

import java.util.List;

@Repository
public interface SupplierRepository extends BaseRepository<Supplier, Long> {
    @Query("from Supplier s " +
           "where 1=1 " +
           "and (coalesce(:ignoreIds) is null or s.id not in (:ignoreIds))")
    Page<Supplier> findAll(@Param("ignoreIds") List<Long> ignoreIds, Pageable pageable);
}