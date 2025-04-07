package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.base.service.BaseGService;
import com.flowiee.pms.entity.sales.GarmentFactory;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.model.dto.GarmentFactoryDTO;
import com.flowiee.pms.repository.sales.GarmentFactoryRepository;
import com.flowiee.pms.service.sales.GarmentFactoryService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GarmentFactoryServiceImpl extends BaseGService<GarmentFactory, GarmentFactoryDTO, GarmentFactoryRepository>
        implements GarmentFactoryService {
    public GarmentFactoryServiceImpl(GarmentFactoryRepository pEntityRepository) {
        super(GarmentFactory.class, GarmentFactoryDTO.class, pEntityRepository);
    }

    @Override
    public List<GarmentFactoryDTO> findAll() {
        return super.findAll();
    }

    @Override
    public GarmentFactoryDTO findById(Long pEntityId, boolean pThrowException) {
       return super.findById(pEntityId, pThrowException);
    }

    @Override
    public GarmentFactoryDTO save(GarmentFactoryDTO pEntity) {
        if (pEntity == null) {
            throw new BadRequestException();
        }
        return super.save(pEntity);
    }

    @Override
    public GarmentFactoryDTO update(GarmentFactoryDTO pEntity, Long pEntityId) {
        return super.update(pEntity, pEntityId);
    }

    @Override
    public String delete(Long pEntityId) {
        return super.delete(pEntityId);
    }
}