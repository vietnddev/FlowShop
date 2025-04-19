package com.flowiee.pms.modules.system.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.system.entity.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends BaseRepository<Notification, Long> {
    @Query("from Notification n where n.receive=:accountId order by n.id desc")
    List<Notification> findAllByReceiveId(@Param("accountId") Long accountId);

    @Query(value = "select * from notification n where n.receive=:accountId order by n.id desc fetch next :limit row only", nativeQuery = true)
    List<Notification> findLimitByReceiveId(@Param("accountId") Long accountId, @Param("limit") Integer limit);
}