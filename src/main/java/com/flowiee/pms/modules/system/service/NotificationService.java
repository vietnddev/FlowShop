package com.flowiee.pms.modules.system.service;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.system.dto.NotificationDTO;

import java.util.List;

public interface NotificationService extends ICurdService<NotificationDTO> {
    List<NotificationDTO> findAllByReceiveId(Integer pageSize, Integer pageNum, Integer totalRecord, Long accountId);

    List<NotificationDTO> findLimitByReceiveId(Long accountId, Integer limit);
}