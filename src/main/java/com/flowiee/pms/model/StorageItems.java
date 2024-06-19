package com.flowiee.pms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StorageItems {
    Integer storageId;
    Integer itemId;
    String itemImageSrc;
    String itemName;
    String itemType;
    String itemBrand;
    Integer itemSalesAvailableQty;
    Integer itemStorageQty;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime lastImportTime;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime firstImportTime;
    String isProduct;
}