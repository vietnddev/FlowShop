package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.entity.sales.Supplier;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.repository.sales.SupplierRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.sales.SupplierService;

import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
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

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SupplierServiceImpl extends BaseService implements SupplierService {
    SupplierRepository mvSupplierRepository;

    @Override
    public List<Supplier> findAll() {
        return this.findAll(-1, -1, null).getContent();
    }

    @Override
    public Page<Supplier> findAll(Integer pageSize, Integer pageNum, List<Long> ignoreIds) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("name").ascending());
        return mvSupplierRepository.findAll(ignoreIds, pageable);
    }

    @Override
    public Supplier findById(Long entityId, boolean pThrowException) {
        Optional<Supplier> entityOptional = mvSupplierRepository.findById(entityId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"supplier"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public Supplier save(Supplier supplier) {
        supplier.setStatus("A");
        return mvSupplierRepository.save(supplier);
    }

    @Override
    public Supplier update(Supplier entity, Long entityId) {
        Supplier supplier = this.findById(entityId, true);

        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(supplier));

        entity.setId(entityId);
        Supplier supplierUpdated = mvSupplierRepository.save(entity);

        changeLog.setNewObject(supplierUpdated);
        changeLog.doAudit();

        systemLogService.writeLogUpdate(MODULE.SALES, ACTION.PRO_SUP_U, MasterObject.Supplier, "Cập nhật thông tin nhà cung cấp: " + supplierUpdated.getName(), changeLog);

        return supplierUpdated;
    }

    @Override
    public String delete(Long entityId) {
        if (entityId == null || entityId <= 0) {
            throw new BadRequestException();
        }
        mvSupplierRepository.deleteById(entityId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }
}