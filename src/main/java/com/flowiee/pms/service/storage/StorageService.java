package com.flowiee.pms.service.storage;

import com.flowiee.pms.base.service.BaseCurdService;
import com.flowiee.pms.model.StorageItems;
import com.flowiee.pms.model.dto.StorageDTO;
import org.springframework.data.domain.Page;

public interface StorageService extends BaseCurdService<StorageDTO> {
    Page<StorageDTO> findAll(int pageSize, int pageNum);

    Page<StorageItems> findStorageItems(int pageSize, int pageNum, Long storageId, String searchText);
}