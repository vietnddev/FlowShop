package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.entity.product.Material;
import com.flowiee.pms.entity.product.MaterialHistory;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.repository.product.MaterialHistoryRepository;
import com.flowiee.pms.service.product.MaterialHistoryService;

import com.flowiee.pms.common.enumeration.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MaterialHistoryServiceImpl implements MaterialHistoryService {
    MaterialHistoryRepository mvMaterialHistoryRepository;

    @Override
    public List<MaterialHistory> findAll() {
        return mvMaterialHistoryRepository.findAll();
    }

    @Override
    public MaterialHistory findById(Long entityId, boolean pThrowException) {
        Optional<MaterialHistory> entityOptional = mvMaterialHistoryRepository.findById(entityId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"material history"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public MaterialHistory save(MaterialHistory entity) {
        if (entity == null) {
            throw new BadRequestException();
        }
        return mvMaterialHistoryRepository.save(entity);
    }

    @Override
    public MaterialHistory update(MaterialHistory entity, Long entityId) {
        if (entity == null || entityId == null || entityId <= 0) {
            throw new BadRequestException();
        }
        entity.setId(entityId);
        return mvMaterialHistoryRepository.save(entity);
    }

    @Override
    public String delete(Long entityId) {
        if (entityId == null || entityId <= 0) {
            throw new BadRequestException();
        }
        mvMaterialHistoryRepository.deleteById(entityId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public List<MaterialHistory> findByMaterialId(Long materialId) {
        return mvMaterialHistoryRepository.findByMaterialId(materialId);
    }

    @Override
    public List<MaterialHistory> findByFieldName(String fieldName) {
        return mvMaterialHistoryRepository.findByFieldName(fieldName);
    }

    @Override
    public List<MaterialHistory> save(Map<String, Object[]> logChanges, String title, Long materialId) {
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
            materialHistories.add(this.save(materialHistory));
        }
        return materialHistories;
    }
}