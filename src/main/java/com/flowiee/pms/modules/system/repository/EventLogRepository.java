package com.flowiee.pms.modules.system.repository;

import com.flowiee.pms.modules.system.entity.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventLogRepository extends JpaRepository<EventLog, Long> {
    @Query("from EventLog el where el.requestId = :requestId")
    EventLog findByRequestId(@Param("requestId") Long requestId);

    @Query("from EventLog el where el.createdTime <= :createdTime")
    List<EventLog> getEventLogFrom(@Param("createdTime") LocalDateTime createdTime);
}