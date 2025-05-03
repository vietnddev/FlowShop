package com.flowiee.pms.modules.promotion.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.promotion.entity.VoucherInfo;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VoucherInfoRepository extends BaseRepository<VoucherInfo, Long> {
    @Query("from VoucherInfo v " +
           "where 1=1 " +
           "and (:ids is null or v.id in :ids) " +
           "and (:title is null or v.title like %:title%) " +
           "and ((:startTime is null and :endTime is null) or ((v.startTime >= :startTime) and (v.endTime <= :endTime))) " +
           "and (:status is null or (:status = 'I' or (case when ((function('date', v.startTime) <= function('date', current_date)) and (function('date', v.endTime) >= function('date', current_date))) then 'A' else 'I' end) = 'A')) " +
           "and (:status is null or (:status = 'A' or (case when ((function('date', v.startTime) <= function('date', current_date)) and (function('date', v.endTime) >= function('date', current_date))) then 'A' else 'I' end) = 'I'))")
    Page<VoucherInfo> findAll(@Param("ids") List<Long> voucherIds,
                              @Param("title") String title,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime,
                              @Param("status") String status,
                              Pageable pageable);
}