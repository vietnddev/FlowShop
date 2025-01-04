package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.entity.product.Material;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.common.ChangeLog;
import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.repository.product.MaterialRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.product.MaterialHistoryService;
import com.flowiee.pms.service.product.MaterialService;

import com.flowiee.pms.common.enumeration.MasterObject;
import com.flowiee.pms.common.enumeration.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MaterialServiceImpl extends BaseService implements MaterialService {
    MaterialRepository     mvMaterialRepository;
    MaterialHistoryService mvMaterialHistoryService;

    @Override
    public List<Material> findAll() {
        return this.findAll(-1, -1, null, null, null, null, null, null).getContent();
    }

    @Override
    public Page<Material> findAll(int pageSize, int pageNum, Long supplierId, Long unitId, String code, String name, String location, String status) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("name").ascending());
        return mvMaterialRepository.findAll(supplierId, unitId, code, name, location, status, pageable);
    }

    @Override
    public Material findById(Long entityId, boolean pThrowException) {
        Optional<Material> entityOptional = mvMaterialRepository.findById(entityId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"material"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public Material save(Material entity) {
        Material materialSaved = mvMaterialRepository.save(entity);
        systemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.STG_MAT_C, MasterObject.Material, "Thêm mới nguyên vật liệu", materialSaved.getName());
        return materialSaved;
    }

    @Override
    public Material update(Material entity, Long materialId) {
        Material materialOptional = this.findById(materialId, true);

        Material materialBefore = ObjectUtils.clone(materialOptional);
        entity.setId(materialId);
        Material materialUpdated = mvMaterialRepository.save(entity);

        String logTitle = "Cập nhật nguyên vật liệu: " + materialUpdated.getName();
        ChangeLog changeLog = new ChangeLog(materialBefore, materialUpdated);
        mvMaterialHistoryService.save(changeLog.getLogChanges(), logTitle, materialId);
        systemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.STG_MAT_U, MasterObject.Material, logTitle, changeLog);
        logger.info(logTitle);

        return materialUpdated;
    }

    @Override
    public String delete(Long entityId) {
        Material materialToDelete = this.findById(entityId, true);

        mvMaterialRepository.deleteById(materialToDelete.getId());

        String logTitle = "Xóa nguyên vật liệu";
        systemLogService.writeLogDelete(MODULE.PRODUCT, ACTION.STG_MAT_U, MasterObject.Material, "Xóa nguyên vật liệu", materialToDelete.getName());
        logger.info("{}: {}", logTitle, materialToDelete.getName());

        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Transactional
    @Override
    public void updateQuantity(Integer quantity, long materialId, String type) {
        String logTitle = "Cập nhật số lượng nguyên vật liệu";
        if ("I".equals(type)) {
            mvMaterialRepository.updateQuantityIncrease(quantity, materialId);
            systemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.STG_MAT_U, MasterObject.Material, logTitle, " + " + quantity);
        } else if ("D".equals(type)) {
            mvMaterialRepository.updateQuantityDecrease(quantity, materialId);
            systemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.STG_MAT_U, MasterObject.Material, logTitle, " - " + quantity);
        }
    }
}