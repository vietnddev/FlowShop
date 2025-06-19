package com.flowiee.pms.modules.system.repository;

import com.flowiee.pms.modules.system.schedule.entity.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleStatusRepository extends JpaRepository<ScheduleStatus, Long> {
}