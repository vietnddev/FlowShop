package com.flowiee.pms.repository.sales;

import com.flowiee.pms.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.entity.sales.VoucherInfo;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VoucherInfoRepository extends BaseRepository<VoucherInfo, Long> {
    @Query("from VoucherInfo v " +
           "where 1=1 " +
           "and (:ids is null or v.id in :ids) " +
           "and (:title is null or v.title like %:title%) " +
           "and ((:startTime is null and :endTime is null) or ((v.startTime >= :startTime) and (v.endTime <= :endTime))) " +
           "and (:status is null or (:status = 'I' or (case when ((trunc(v.startTime) <= trunc(current_date)) and (trunc(v.endTime) >= trunc(current_date))) then 'A' else 'I' end) = 'A')) " +
           "and (:status is null or (:status = 'A' or (case when ((trunc(v.startTime) <= trunc(current_date)) and (trunc(v.endTime) >= trunc(current_date))) then 'A' else 'I' end) = 'I'))")
    Page<VoucherInfo> findAll(@Param("ids") List<Long> voucherIds,
                              @Param("title") String title,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime,
                              @Param("status") String status,
                              Pageable pageable);
}