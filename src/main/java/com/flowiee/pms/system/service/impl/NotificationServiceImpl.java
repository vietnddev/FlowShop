package com.flowiee.pms.system.service.impl;

import com.flowiee.pms.shared.base.BaseService;
import com.flowiee.pms.shared.request.BaseParameter;
import com.flowiee.pms.system.entity.Notification;
import com.flowiee.pms.shared.exception.BadRequestException;
import com.flowiee.pms.system.dto.NotificationDTO;
import com.flowiee.pms.system.repository.NotificationRepository;

import com.flowiee.pms.system.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl extends BaseService<Notification, NotificationDTO, NotificationRepository> implements NotificationService {
    public NotificationServiceImpl(NotificationRepository pEntityRepository) {
        super(Notification.class, NotificationDTO.class, pEntityRepository);
    }

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public List<NotificationDTO>find() {
        return super.find(BaseParameter.builder().build());
    }

    @Override
    public List<NotificationDTO> findAllByReceiveId(Integer pageSize, Integer pageNum, Integer totalRecord, Long accountId) {
        if (totalRecord != null) {
            return convertDTOs(mvEntityRepository.findLimitByReceiveId(accountId, totalRecord));
        }
        return convertDTOs(mvEntityRepository.findAllByReceiveId(accountId));
    }

    @Override
    public List<NotificationDTO> findLimitByReceiveId(Long accountId, Integer limit) {
        return convertDTOs(mvEntityRepository.findLimitByReceiveId(accountId, limit));
    }

    @Override
    public NotificationDTO findById(Long pNotificationId, boolean pThrowException) {
        return super.findDtoById(pNotificationId, pThrowException);
    }

    @Override
    public NotificationDTO save(NotificationDTO pNotification) {
        if (pNotification == null) {
            throw new BadRequestException();
        }
        NotificationDTO notificationSaved = super.save(pNotification);
        LOG.info(NotificationServiceImpl.class.getName() + ": Insert notification " + pNotification.toString());
        return notificationSaved;
    }

    @Override
    public NotificationDTO update(NotificationDTO pDto, Long pId) {
        if (pDto == null || pId == null || pId <= 0) {
            throw new BadRequestException();
        }
        return super.update(pDto, pId);
    }

    @Override
    public String delete(Long pNotifyId) {
        return super.delete(pNotifyId);
    }
}