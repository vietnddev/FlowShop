package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.base.service.BaseGService;
import com.flowiee.pms.modules.product.entity.Material;
import com.flowiee.pms.modules.product.entity.MaterialHistory;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.product.dto.MaterialHistoryDTO;
import com.flowiee.pms.modules.product.repository.MaterialHistoryRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MaterialHistoryServiceImpl extends BaseGService<MaterialHistory, MaterialHistoryDTO, MaterialHistoryRepository> implements MaterialHistoryService {
    public MaterialHistoryServiceImpl(MaterialHistoryRepository pMaterialHistoryRepository) {
        super(MaterialHistory.class, MaterialHistoryDTO.class, pMaterialHistoryRepository);
    }

    @Override
    public List<MaterialHistoryDTO> findAll() {
        return super.findAll();
    }

    @Override
    public MaterialHistoryDTO findById(Long pEntityId, boolean pThrowException) {
        return super.findById(pEntityId, pThrowException);
    }

    @Override
    public MaterialHistoryDTO save(MaterialHistoryDTO pEntity) {
        if (pEntity == null) {
            throw new BadRequestException();
        }
        return super.save(pEntity);
    }

    @Override
    public MaterialHistoryDTO update(MaterialHistoryDTO pEntity, Long pEntityId) {
        if (pEntity == null || pEntityId == null || pEntityId <= 0) {
            throw new BadRequestException();
        }
        pEntity.setId(pEntityId);
        return super.update(pEntity, pEntityId);
    }

    @Override
    public String delete(Long pEntityId) {
        if (pEntityId == null || pEntityId <= 0) {
            throw new BadRequestException();
        }
        return super.delete(pEntityId);
    }

    @Override
    public List<MaterialHistoryDTO> findByMaterialId(Long materialId) {
        return super.convertDTOs(mvEntityRepository.findByMaterialId(materialId));
    }

    @Override
    public List<MaterialHistoryDTO> findByFieldName(String fieldName) {
        return super.convertDTOs(mvEntityRepository.findByFieldName(fieldName));
    }

    @Override
    public List<MaterialHistoryDTO> save(Map<String, Object[]> logChanges, String title, Long materialId) {
        List<MaterialHistory> materialHistories = new ArrayList<>();
        for (Map.Entry<String, Object[]> entry : logChanges.entrySet()) {
            String field = entry.getKey();
            String oldValue = entry.getValue()[0].toString();
            String newValue = entry.getValue()[1].toString();
            MaterialHistory materialHistory = MaterialHistory.builder()
                    .title("Update material")
                    .material(new Material(materialId))
                    .fieldName(field)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .build();
            materialHistories.add(mvEntityRepository.save(materialHistory));
        }
        return convertDTOs(materialHistories);
    }
}