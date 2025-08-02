package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.modules.inventory.entity.Storage;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.common.exception.EntityNotFoundException;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.modules.inventory.model.StorageItems;
import com.flowiee.pms.modules.inventory.dto.StorageDTO;
import com.flowiee.pms.modules.inventory.repository.StorageRepository;
import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.modules.inventory.service.StorageService;
import com.flowiee.pms.modules.system.service.SystemLogService;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;

@Service
public class StorageServiceImpl extends BaseService<Storage, StorageDTO, StorageRepository> implements StorageService {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final SystemLogService systemLogService;

    public StorageServiceImpl(StorageRepository pEntityRepository, SystemLogService systemLogService) {
        super(Storage.class, StorageDTO.class, pEntityRepository);
        this.systemLogService = systemLogService;
    }

    @Override
    public Page<StorageDTO> find(int pageSize, int pageNum) {
        Pageable pageable = getPageable(pageNum, pageSize);
        Page<Storage> storages = mvEntityRepository.findAll(pageable);
        return new PageImpl<>(StorageDTO.convertToDTOs(storages.getContent()), pageable, storages.getTotalElements());
    }

    @Override
    public Page<StorageItems> findStorageItems(int pageSize, int pageNum, Long storageId, String searchText) {
        Optional<Storage> storage = super.findById(storageId);
        if (storage.isEmpty())
            throw new BadRequestException("Storage not found");
        Pageable pageable = getPageable(pageNum, pageSize);
        Page<Object[]> storageItemsRawData = mvEntityRepository.findAllItems(searchText, storageId, pageable);
        List<StorageItems> storageItems = new ArrayList<>();
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true)
                .optionalEnd()
                .toFormatter();
        for (Object[] object : storageItemsRawData) {
            StorageItems s = StorageItems.builder()
                    .storageId(storageId)
                    .isProduct(object[0].toString())
                    .itemId(Long.parseLong(object[1].toString()))
                    .itemImageSrc(object[2] != null ? object[2].toString() : null)
                    .itemName(object[3].toString())
                    .itemType(object[4] != null ? object[4].toString() : "")
                    .itemBrand(object[5] != null ? object[5].toString() : "")
                    .build();
            if (object[6] != null) s.setItemStorageQty(Integer.parseInt(String.valueOf(object[6])));
            if (object[7] != null) s.setItemSalesAvailableQty(Integer.parseInt(String.valueOf(object[7])));
            if (object[8] != null) s.setFirstImportTime(LocalDateTime.parse(Objects.toString(object[8]), formatter));
            if (object[9] != null) s.setLastImportTime(LocalDateTime.parse(Objects.toString(object[9]), formatter));
            storageItems.add(s);
        }
        return new PageImpl<>(storageItems, pageable, storageItemsRawData.getTotalElements());
    }

    @Override
    public Storage findEntById(Long pStorageId, boolean pThrowException) {
        return super.findEntById(pStorageId, pThrowException);
    }

    @Override
    public StorageDTO findById(Long storageId, boolean pThrowException) {
        Optional<Storage> storageOptional = super.findById(storageId);
        if (storageOptional.isPresent()) {
            List<StorageItems> storageItemsList = this.findStorageItems(-1, -1, storageId, null).getContent();
            StorageDTO storage = StorageDTO.convertToDTO(storageOptional.get());
            storage.setListStorageItems(storageItemsList);
            storage.setTotalItems(storageItemsList.size());
            storage.setTotalInventoryValue(BigDecimal.ZERO);
            return storage;
        }
        if (pThrowException) {
            throw new EntityNotFoundException(new Object[] {"storage"}, null, null);
        } else {
            return null;
        }
    }

    @Override
    public StorageDTO save(StorageDTO pStorageDTO) {
        String lvCode = pStorageDTO.getCode();

        if (mvEntityRepository.findByCode(lvCode) != null)
            throw new BadRequestException(String.format("Storage code %s existed!", lvCode));

        pStorageDTO.setStatus("Y");

        return super.save(pStorageDTO);
    }

    @Override
    public StorageDTO update(StorageDTO inputStorageDTO, Long storageId) {
        Optional<Storage> storageOpt = super.findById(storageId);
        if (storageOpt.isEmpty()) {
            throw new BadRequestException("Storage not found");
        }
        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(storageOpt.get()));

        storageOpt.get().setName(inputStorageDTO.getName());
        storageOpt.get().setLocation(inputStorageDTO.getLocation());
        storageOpt.get().setDescription(inputStorageDTO.getDescription());
        storageOpt.get().setIsDefault(inputStorageDTO.getIsDefault());
        storageOpt.get().setStatus(inputStorageDTO.getStatus());
        Storage storageUpdated = mvEntityRepository.save(storageOpt.get());

        changeLog.setNewObject(storageUpdated);
        changeLog.doAudit();

        systemLogService.writeLogUpdate(MODULE.STORAGE, ACTION.STG_STG_U, MasterObject.Storage, "Cập nhật Kho", changeLog);

        return StorageDTO.convertToDTO(storageUpdated);
    }

    @Override
    public String delete(Long storageId) {
        try {
            Storage storage = this.findEntById(storageId, true);
            if ("Y".equals(storage.getStatus())) {
                return "This storage is in use!";
            }
            mvEntityRepository.deleteById(storageId);
            systemLogService.writeLogDelete(MODULE.STORAGE, ACTION.STG_STORAGE, MasterObject.Storage, "Xóa kho", storage.getName());
            LOG.info("Delete storage success! storageId={}", storageId);
            return MessageCode.DELETE_SUCCESS.getDescription();
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.DELETE_ERROR_OCCURRED.getDescription(), "Storage storageId=" + storageId), ex);
        }
    }
}