package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.entity.sales.GarmentFactory;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.repository.sales.GarmentFactoryRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.sales.GarmentFactoryService;

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
public class GarmentFactoryServiceImpl extends BaseService implements GarmentFactoryService {
    GarmentFactoryRepository mvGarmentFactoryRepository;

    @Override
    public List<GarmentFactory> findAll() {
        return mvGarmentFactoryRepository.findAll();
    }

    @Override
    public GarmentFactory findById(Long entityId, boolean pThrowException) {
        Optional<GarmentFactory> entityOptional = mvGarmentFactoryRepository.findById(entityId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"garment factory item"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public GarmentFactory save(GarmentFactory entity) {
        if (entity == null) {
            throw new BadRequestException();
        }
        return mvGarmentFactoryRepository.save(entity);
    }

    @Override
    public GarmentFactory update(GarmentFactory entity, Long entityId) {
        if (this.findById(entityId, true) == null) {
            throw new BadRequestException();
        }
        entity.setId(entityId);
        return mvGarmentFactoryRepository.save(entity);
    }

    @Override
    public String delete(Long entityId) {
        if (this.findById(entityId, true) == null) {
            throw new BadRequestException();
        }
        mvGarmentFactoryRepository.deleteById(entityId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }
}