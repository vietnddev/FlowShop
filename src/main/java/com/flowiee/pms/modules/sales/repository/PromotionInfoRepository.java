package com.flowiee.pms.modules.sales.repository;

import com.flowiee.pms.modules.sales.entity.PromotionInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PromotionInfoRepository extends JpaRepository<PromotionInfo, Long> {
    @Query("from PromotionInfo p " +
            "where 1=1 " +
            "and (:title is null or p.title like %:title%) " +
            "and ((:startTime is null and :endTime is null) or ((p.startTime >= :startTime) and (p.endTime <= :endTime))) " +
            "and (:status is null or (:status = 'I' or (case when ((function('date', p.startTime) <= function('date', current_date)) and (function('date', p.endTime) >= function('date', current_date))) then 'A' else 'I' end) = 'A')) " +
            "and (:status is null or (:status = 'A' or (case when ((function('date', p.startTime) <= function('date', current_date)) and (function('date', p.endTime) >= function('date', current_date))) then 'A' else 'I' end) = 'I'))")
    Page<PromotionInfo> findAll(@Param("title") String title,
                                @Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime,
                                @Param("status") String status,
                                Pageable pageable);
}