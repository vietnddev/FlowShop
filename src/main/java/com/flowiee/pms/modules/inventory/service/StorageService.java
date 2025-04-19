package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.inventory.entity.Storage;
import com.flowiee.pms.modules.inventory.model.StorageItems;
import com.flowiee.pms.modules.inventory.dto.StorageDTO;
import org.springframework.data.domain.Page;

public interface StorageService extends ICurdService<StorageDTO> {
    Page<StorageDTO> findAll(int pageSize, int pageNum);

    Page<StorageItems> findStorageItems(int pageSize, int pageNum, Long storageId, String searchText);

    Storage findEntById(Long pStorageId, boolean pThrowException);
}