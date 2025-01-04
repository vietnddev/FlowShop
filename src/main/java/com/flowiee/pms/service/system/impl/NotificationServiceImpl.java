package com.flowiee.pms.service.system.impl;

import com.flowiee.pms.entity.system.Notification;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.repository.system.NotificationRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.system.NotificationService;

import com.flowiee.pms.common.enumeration.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class NotificationServiceImpl extends BaseService implements NotificationService {
    NotificationRepository mvNotificationRepository;

    @Override
    public List<Notification> findAll() {
        return mvNotificationRepository.findAll();
    }

    @Override
    public List<Notification> findAllByReceiveId(Integer pageSize, Integer pageNum, Integer totalRecord, Long accountId) {
        if (totalRecord != null) {
            return mvNotificationRepository.findLimitByReceiveId(accountId, totalRecord);
        }
        return mvNotificationRepository.findAllByReceiveId(accountId);
    }

    @Override
    public List<Notification> findLimitByReceiveId(Long accountId, Integer limit) {
        return mvNotificationRepository.findLimitByReceiveId(accountId, limit);
    }

    @Override
    public Notification findById(Long notificationId, boolean pThrowException) {
        Optional<Notification> entityOptional = mvNotificationRepository.findById(notificationId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"cart item"}, null, null);
        }
        return entityOptional.orElse(null);

    }

    @Override
    public Notification save(Notification notification) {
        if (notification == null) {
            throw new BadRequestException();
        }
        Notification notificationSaved = mvNotificationRepository.save(notification);
        logger.info(NotificationServiceImpl.class.getName() + ": Insert notification " + notification.toString());
        return notificationSaved;
    }

    @Override
    public Notification update(Notification entity, Long entityId) {
        if (entity == null || entityId == null || entityId <= 0) {
            throw new BadRequestException();
        }
        entity.setId(entityId);
        return mvNotificationRepository.save(entity);
    }

    @Override
    public String delete(Long entityId) {
        Notification notification = this.findById(entityId, true);
        mvNotificationRepository.deleteById(notification.getId());
        return MessageCode.DELETE_SUCCESS.getDescription();
    }
}