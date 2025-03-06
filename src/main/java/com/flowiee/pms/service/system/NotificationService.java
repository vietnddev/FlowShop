package com.flowiee.pms.service.system;

import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.entity.system.Notification;

import java.util.List;

public interface NotificationService extends BaseCurdService<Notification> {
    List<Notification> findAllByReceiveId(Integer pageSize, Integer pageNum, Integer totalRecord, Long accountId);

    List<Notification> findLimitByReceiveId(Long accountId, Integer limit);
}