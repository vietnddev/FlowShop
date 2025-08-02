package com.flowiee.pms.modules.sales.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.modules.sales.entity.GarmentFactory;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.sales.dto.GarmentFactoryDTO;
import com.flowiee.pms.modules.sales.repository.GarmentFactoryRepository;

import com.flowiee.pms.modules.sales.service.GarmentFactoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GarmentFactoryServiceImpl extends BaseService<GarmentFactory, GarmentFactoryDTO, GarmentFactoryRepository>
        implements GarmentFactoryService {
    public GarmentFactoryServiceImpl(GarmentFactoryRepository pEntityRepository) {
        super(GarmentFactory.class, GarmentFactoryDTO.class, pEntityRepository);
    }

    @Override
    public List<GarmentFactoryDTO>find(BaseParameter pParam) {
        return super.find(pParam);
    }

    @Override
    public GarmentFactoryDTO findById(Long pEntityId, boolean pThrowException) {
       return super.findDtoById(pEntityId, pThrowException);
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