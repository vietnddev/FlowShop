package com.flowiee.pms.service.system;

import com.flowiee.pms.base.service.BaseCurdService;
import com.flowiee.pms.entity.system.FileImportHistory;

import java.util.List;

public interface ImportService extends BaseCurdService<FileImportHistory> {
    List<FileImportHistory> findAll();

    List<FileImportHistory> findByAccountId(Long accountId);
}