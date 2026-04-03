package com.flowiee.pms.system.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.system.dto.NotificationDTO;

import java.util.List;

public interface NotificationService extends ICurdService<NotificationDTO> {
    List<NotificationDTO> find();

    List<NotificationDTO> findAllByReceiveId(Integer pageSize, Integer pageNum, Integer totalRecord, Long accountId);

    List<NotificationDTO> findLimitByReceiveId(Long accountId, Integer limit);
}