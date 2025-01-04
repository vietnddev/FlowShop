package com.flowiee.pms.common.converter;

import com.flowiee.pms.entity.storage.Storage;
import com.flowiee.pms.model.dto.StorageDTO;

public class StorageConvert {
    public static Storage convertToEntity(StorageDTO inputDTO) {
        Storage storage = Storage.builder()
            .name(inputDTO.getName())
            .code(inputDTO.getCode())
            .location(inputDTO.getLocation())
            .area(inputDTO.getArea())
            .holdableQty(inputDTO.getHoldableQty())
            .holdWarningPercent(inputDTO.getHoldWarningPercent())
            .description(inputDTO.getDescription())
            .status(inputDTO.getStatus())
            .build();
        storage.setId(inputDTO.getId());
        return storage;
    }
}