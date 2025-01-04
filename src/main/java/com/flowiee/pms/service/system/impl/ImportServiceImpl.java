package com.flowiee.pms.service.system.impl;

import com.flowiee.pms.entity.system.FileImportHistory;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.repository.system.AppImportRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.system.ImportService;

import com.flowiee.pms.common.enumeration.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ImportServiceImpl extends BaseService implements ImportService {
    AppImportRepository mvAppImportRepository;

    @Override
    public List<FileImportHistory> findAll() {
        return mvAppImportRepository.findAll();
    }

    @Override
    public List<FileImportHistory> findByAccountId(Long accountId) {
        return mvAppImportRepository.findByAccountId(accountId);
    }

    @Override
    public FileImportHistory findById(Long importId, boolean pThrowException) {
        Optional<FileImportHistory> entityOptional = mvAppImportRepository.findById(importId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"import record"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public FileImportHistory save(FileImportHistory fileImportHistory) {
        if (fileImportHistory == null) {
            throw new BadRequestException();
        }
        return mvAppImportRepository.save(fileImportHistory);
    }

    @Override
    public FileImportHistory update(FileImportHistory entity, Long entityId) {
        if (entity == null || entityId == null || entityId <= 0) {
            throw new BadRequestException();
        }
        entity.setId(entityId);
        return mvAppImportRepository.save(entity);
    }

    @Override
    public String delete(Long entityId) {
        FileImportHistory fImport = this.findById(entityId, true);

        mvAppImportRepository.deleteById(fImport.getId());
        return MessageCode.DELETE_SUCCESS.getDescription();
    }
}