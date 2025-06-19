package com.flowiee.pms.modules.system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.system.entity.SystemLog;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    @Query("from SystemLog s order by s.createdAt desc")
    Page<SystemLog> findAll(Pageable pageable);

    @Query("from SystemLog s where s.createdAt <= :createdTime")
    List<SystemLog> getSystemLogFrom(@Param("createdTime") LocalDateTime createdTime);
}