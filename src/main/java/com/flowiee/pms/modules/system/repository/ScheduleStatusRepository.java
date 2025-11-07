package com.flowiee.pms.modules.system.repository;

import com.flowiee.pms.modules.system.schedule.entity.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleStatusRepository extends JpaRepository<ScheduleStatus, Long> {
    @Query("select s from ScheduleStatus s " +
           "where s.schedule.scheduleId = :scheduleId " +
           "and s.id = (select max(ss.id) from ScheduleStatus ss where ss.schedule.scheduleId = :scheduleId)")
    ScheduleStatus findLatestByScheduleId(@Param("scheduleId") String scheduleId);
}