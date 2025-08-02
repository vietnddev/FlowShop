package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.modules.inventory.entity.Material;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.modules.inventory.dto.MaterialDTO;
import com.flowiee.pms.modules.inventory.repository.MaterialRepository;
import com.flowiee.pms.common.base.service.BaseService;

import com.flowiee.pms.common.enumeration.MasterObject;
import com.flowiee.pms.common.enumeration.MessageCode;
import com.flowiee.pms.modules.inventory.service.MaterialHistoryService;
import com.flowiee.pms.modules.inventory.service.MaterialService;
import com.flowiee.pms.modules.system.service.SystemLogService;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MaterialServiceImpl extends BaseService<Material, MaterialDTO, MaterialRepository> implements MaterialService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final MaterialHistoryService mvMaterialHistoryService;
    private final SystemLogService systemLogService;
    private final ModelMapper modelMapper;

    public MaterialServiceImpl(MaterialRepository pMaterialRepository,
                               MaterialHistoryService pMaterialHistoryService,
                               SystemLogService pSystemLogService,
                               ModelMapper pModelMapper) {
        super(Material.class, MaterialDTO.class, pMaterialRepository);
        this.mvMaterialHistoryService = pMaterialHistoryService;
        this.systemLogService = pSystemLogService;
        this.modelMapper = pModelMapper;
    }

    @Override
    public List<MaterialDTO>find(BaseParameter pParam) {
        return this.find(-1, -1, null, null, null, null, null, null).getContent();
    }

    @Override
    public Page<MaterialDTO> find(int pageSize, int pageNum, Long supplierId, Long unitId, String code, String name, String location, String status) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("name").ascending());
        Page<Material> materialPage = mvEntityRepository.findAll(supplierId, unitId, code, name, location, status, pageable);
        return new PageImpl<>(convertDTOs(materialPage.getContent()), pageable, materialPage.getTotalElements());
    }

    @Override
    public MaterialDTO findById(Long entityId, boolean pThrowException) {
        return super.findDtoById(entityId, pThrowException);
    }

    @Override
    public MaterialDTO save(MaterialDTO pDto) {
        MaterialDTO materialSaved = super.save(pDto);
        systemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.STG_MAT_C, MasterObject.Material, "Thêm mới nguyên vật liệu", materialSaved.getName());
        return materialSaved;
    }

    @Override
    public MaterialDTO update(MaterialDTO pDto, Long materialId) {
        Material lvMaterial = super.findEntById(materialId, true);

        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(lvMaterial));

        //lvMaterial.set...
        Material materialUpdated = mvEntityRepository.save(lvMaterial);

        changeLog.setNewObject(materialUpdated);
        changeLog.doAudit();

        String logTitle = "Cập nhật nguyên vật liệu: " + materialUpdated.getName();
        mvMaterialHistoryService.save(changeLog.getLogChanges(), logTitle, materialId);
        systemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.STG_MAT_U, MasterObject.Material, logTitle, changeLog);
        logger.info(logTitle);

        return modelMapper.map(materialUpdated, MaterialDTO.class);
    }

    @Override
    public String delete(Long entityId) {
        Material materialToDelete = super.findEntById(entityId, true);

        mvEntityRepository.deleteById(materialToDelete.getId());

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
            mvEntityRepository.updateQuantityIncrease(quantity, materialId);
            systemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.STG_MAT_U, MasterObject.Material, logTitle, " + " + quantity);
        } else if ("D".equals(type)) {
            mvEntityRepository.updateQuantityDecrease(quantity, materialId);
            systemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.STG_MAT_U, MasterObject.Material, logTitle, " - " + quantity);
        }
    }
}