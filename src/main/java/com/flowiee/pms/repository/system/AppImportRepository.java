package com.flowiee.pms.repository.system;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.entity.system.ImportHistory;

import java.util.Date;
import java.util.List;

@Repository
public interface AppImportRepository extends JpaRepository<ImportHistory, Long> {
    @Query("from ImportHistory i where i.account.id=:accountId")
    List<ImportHistory> findByAccountId(@Param("accountId") long accountId);

    @Query("from ImportHistory i where i.beginTime=:startTime")
    ImportHistory findByStartTime(@Param("startTime") Date startTime);
}