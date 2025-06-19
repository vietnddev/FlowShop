package com.flowiee.pms.modules.sales.service.impl;

import com.flowiee.pms.modules.sales.entity.Supplier;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.modules.sales.dto.SupplierDTO;
import com.flowiee.pms.modules.sales.repository.SupplierRepository;
import com.flowiee.pms.common.base.service.BaseService;

import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.common.enumeration.MasterObject;
import com.flowiee.pms.modules.sales.service.SupplierService;
import com.flowiee.pms.modules.system.service.SystemLogService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierServiceImpl extends BaseService<Supplier, SupplierDTO, SupplierRepository> implements SupplierService {
    private final SystemLogService mvSystemLogService;

    public SupplierServiceImpl(SupplierRepository pSupplierRepository, SystemLogService pSystemLogService) {
        super(Supplier.class, SupplierDTO.class, pSupplierRepository);
        this.mvSystemLogService = pSystemLogService;
    }

    @Override
    public List<SupplierDTO> findAll() {
        return this.findAll(-1, -1, null).getContent();
    }

    @Override
    public Page<SupplierDTO> findAll(Integer pageSize, Integer pageNum, List<Long> ignoreIds) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("name").ascending());
        Page<Supplier> supplierPage = mvEntityRepository.findAll(ignoreIds, pageable);
        return new PageImpl<>(convertDTOs(supplierPage.getContent()), pageable, supplierPage.getTotalElements());
    }

    @Override
    public SupplierDTO findById(Long entityId, boolean pThrowException) {
        return super.findDtoById(entityId, pThrowException);
    }

    @Override
    public SupplierDTO save(SupplierDTO pSupplier) {
        pSupplier.setStatus("A");
        return super.save(pSupplier);
    }

    @Override
    public SupplierDTO update(SupplierDTO pDto, Long entityId) {
        Supplier lvCurrentSupplier = super.findEntById(entityId, true);

        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(lvCurrentSupplier));

        lvCurrentSupplier.setName(pDto.getName());
        lvCurrentSupplier.setPhone(pDto.getPhone());
        lvCurrentSupplier.setAddress(pDto.getAddress());
        lvCurrentSupplier.setProductProvided(pDto.getProductProvided());
        lvCurrentSupplier.setNote(pDto.getNote());
        Supplier lvSupplierUpdated = mvEntityRepository.save(lvCurrentSupplier);

        changeLog.setNewObject(lvSupplierUpdated);
        changeLog.doAudit();

        mvSystemLogService.writeLogUpdate(MODULE.SALES, ACTION.PRO_SUP_U, MasterObject.Supplier, "Cập nhật thông tin nhà cung cấp: " + lvSupplierUpdated.getName(), changeLog);

        return super.convertDTO(lvSupplierUpdated);
    }

    @Override
    public String delete(Long pEntityId) {
        if (pEntityId == null || pEntityId <= 0) {
            throw new BadRequestException();
        }
        setAutoAudit(true);
        return super.delete(pEntityId);
    }
}