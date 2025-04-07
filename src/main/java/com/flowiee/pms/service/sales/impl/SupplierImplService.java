package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.entity.sales.Supplier;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.model.dto.SupplierDTO;
import com.flowiee.pms.repository.sales.SupplierRepository;
import com.flowiee.pms.base.service.BaseGService;
import com.flowiee.pms.service.sales.SupplierService;

import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.common.enumeration.MasterObject;
import com.flowiee.pms.service.system.SystemLogService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierImplService extends BaseGService<Supplier, SupplierDTO, SupplierRepository> implements SupplierService {
    private final SystemLogService mvSystemLogService;

    public SupplierImplService(SupplierRepository pSupplierRepository, SystemLogService pSystemLogService) {
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
        return super.findById(entityId, pThrowException);
    }

    @Override
    public SupplierDTO save(SupplierDTO pSupplier) {
        pSupplier.setStatus("A");
        return super.save(pSupplier);
    }

    @Override
    public SupplierDTO update(SupplierDTO pDto, Long entityId) {
        Supplier lvCurrentSupplier = super.findById(entityId).orElseThrow(() -> new BadRequestException());

        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(lvCurrentSupplier));

        lvCurrentSupplier.setName(pDto.getName());
        lvCurrentSupplier.setPhone(pDto.getPhone());
        lvCurrentSupplier.setAddress(pDto.getAddress());
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
        return super.delete(pEntityId);
    }
}