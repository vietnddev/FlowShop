package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.entity.product.Material;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.model.dto.MaterialDTO;
import com.flowiee.pms.repository.product.MaterialRepository;
import com.flowiee.pms.base.service.BaseGService;
import com.flowiee.pms.service.product.MaterialHistoryService;
import com.flowiee.pms.service.product.MaterialService;

import com.flowiee.pms.common.enumeration.MasterObject;
import com.flowiee.pms.common.enumeration.MessageCode;
import com.flowiee.pms.service.system.SystemLogService;
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
public class MaterialImplService extends BaseGService<Material, MaterialDTO, MaterialRepository> implements MaterialService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final MaterialHistoryService mvMaterialHistoryService;
    private final SystemLogService systemLogService;
    private final ModelMapper modelMapper;

    public MaterialImplService(MaterialRepository pMaterialRepository,
                               MaterialHistoryService pMaterialHistoryService,
                               SystemLogService pSystemLogService,
                               ModelMapper pModelMapper) {
        super(Material.class, MaterialDTO.class, pMaterialRepository);
        this.mvMaterialHistoryService = pMaterialHistoryService;
        this.systemLogService = pSystemLogService;
        this.modelMapper = pModelMapper;
    }

    @Override
    public List<MaterialDTO> findAll() {
        return this.findAll(-1, -1, null, null, null, null, null, null).getContent();
    }

    @Override
    public Page<MaterialDTO> findAll(int pageSize, int pageNum, Long supplierId, Long unitId, String code, String name, String location, String status) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("name").ascending());
        Page<Material> materialPage = mvEntityRepository.findAll(supplierId, unitId, code, name, location, status, pageable);
        return new PageImpl<>(convertDTOs(materialPage.getContent()), pageable, materialPage.getTotalElements());
    }

    @Override
    public MaterialDTO findById(Long entityId, boolean pThrowException) {
        return super.findById(entityId, pThrowException);
    }

    @Override
    public MaterialDTO save(MaterialDTO pDto) {
        MaterialDTO materialSaved = super.save(pDto);
        systemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.STG_MAT_C, MasterObject.Material, "Thêm mới nguyên vật liệu", materialSaved.getName());
        return materialSaved;
    }

    @Override
    public MaterialDTO update(MaterialDTO pDto, Long materialId) {
        MaterialDTO materialDto = super.findById(materialId, true);

        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(materialDto));

        pDto.setId(materialId);
        Material materialUpdated = mvEntityRepository.save(modelMapper.map(pDto, Material.class));

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
        MaterialDTO materialToDelete = super.findById(entityId, true);

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