package com.flowiee.pms.service.storage.impl;

import com.flowiee.pms.entity.storage.Storage;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.common.ChangeLog;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.model.StorageItems;
import com.flowiee.pms.model.dto.StorageDTO;
import com.flowiee.pms.repository.storage.StorageRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.storage.StorageService;
import com.flowiee.pms.common.converter.StorageConvert;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StorageServiceImpl extends BaseService implements StorageService {
    StorageRepository mvStorageRepository;

    @Override
    public List<StorageDTO> findAll() {
        return this.findAll(-1, -1).getContent();
    }

    @Override
    public Page<StorageDTO> findAll(int pageSize, int pageNum) {
        Pageable pageable = getPageable(pageNum, pageSize);
        Page<Storage> storages = mvStorageRepository.findAll(pageable);
        return new PageImpl<>(StorageDTO.convertToDTOs(storages.getContent()), pageable, storages.getTotalElements());
    }

    @Override
    public Page<StorageItems> findStorageItems(int pageSize, int pageNum, Long storageId, String searchText) {
        Optional<Storage> storage = mvStorageRepository.findById(storageId);
        if (storage.isEmpty())
            throw new BadRequestException("Storage not found");
        Pageable pageable = getPageable(pageNum, pageSize);
        Page<Object[]> storageItemsRawData = mvStorageRepository.findAllItems(searchText, storageId, pageable);
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
    public StorageDTO findById(Long storageId, boolean pThrowException) {
        Optional<Storage> storageOptional = mvStorageRepository.findById(storageId);
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
    public StorageDTO save(StorageDTO inputStorageDTO) {
        Storage storage = StorageConvert.convertToEntity(inputStorageDTO);
        String lvCode = storage.getCode();

        if (mvStorageRepository.findByCode(lvCode) != null)
            throw new BadRequestException(String.format("Storage code %s existed!", lvCode));

        storage.setStatus("Y");

        Storage storageSaved = mvStorageRepository.save(storage);
        return StorageDTO.convertToDTO(storageSaved);
    }

    @Override
    public StorageDTO update(StorageDTO inputStorageDTO, Long storageId) {
        Optional<Storage> storageOpt = mvStorageRepository.findById(storageId);
        if (storageOpt.isEmpty()) {
            throw new BadRequestException("Storage not found");
        }
        Storage storageBefore = ObjectUtils.clone(storageOpt.get());

        storageOpt.get().setName(inputStorageDTO.getName());
        storageOpt.get().setLocation(inputStorageDTO.getLocation());
        storageOpt.get().setDescription(inputStorageDTO.getDescription());
        storageOpt.get().setIsDefault(inputStorageDTO.getIsDefault());
        storageOpt.get().setStatus(inputStorageDTO.getStatus());

        Storage storageUpdated = mvStorageRepository.save(storageOpt.get());

        ChangeLog changeLog = new ChangeLog(storageBefore, storageUpdated);
        systemLogService.writeLogUpdate(MODULE.STORAGE, ACTION.STG_STG_U, MasterObject.Storage, "Cập nhật Kho", changeLog);

        return StorageDTO.convertToDTO(storageUpdated);
    }

    @Override
    public String delete(Long storageId) {
        try {
            StorageDTO storage = this.findById(storageId, true);
            if ("Y".equals(storage.getStatus())) {
                return "This storage is in use!";
            }
            mvStorageRepository.deleteById(storageId);
            systemLogService.writeLogDelete(MODULE.STORAGE, ACTION.STG_STORAGE, MasterObject.Storage, "Xóa kho", storage.getName());
            logger.info("Delete storage success! storageId={}", storageId);
            return MessageCode.DELETE_SUCCESS.getDescription();
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.DELETE_ERROR_OCCURRED.getDescription(), "Storage storageId=" + storageId), ex);
        }
    }
}