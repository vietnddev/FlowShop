package com.flowiee.pms.common.converter;

import com.flowiee.pms.modules.inventory.entity.Storage;
import com.flowiee.pms.modules.inventory.dto.StorageDTO;

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