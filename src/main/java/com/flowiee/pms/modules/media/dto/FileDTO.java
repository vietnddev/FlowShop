package com.flowiee.pms.modules.media.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.utils.FileUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileDTO extends FileStorage implements Serializable{
    @Serial
    static final long serialVersionUID = 1L;

    Long productId;
    Long productVariantId;
    Long orderId;
    String name;
    String uploadByStr;
    String src;
    Boolean status;
    long size;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime uploadAt;

    public static FileDTO toDto(FileStorage fileStorage) {
        FileDTO dto = new FileDTO();
        if (ObjectUtils.isNotEmpty(fileStorage)) {
            dto.setId(fileStorage.getId());
            if (ObjectUtils.isNotEmpty(fileStorage.getProduct())) {
                dto.setProductId(fileStorage.getProduct().getId());
            }
            if (ObjectUtils.isNotEmpty(fileStorage.getProductDetail())) {
                dto.setProductVariantId(fileStorage.getProductDetail().getId());
            }
            if (ObjectUtils.isNotEmpty(fileStorage.getOrder())) {
                dto.setOrderId(fileStorage.getOrder().getId());
            }
            dto.setSort(fileStorage.getSort());
            dto.setName(fileStorage.getCustomizeName());
            dto.setStorageName(fileStorage.getStorageName());
            dto.setOriginalName(fileStorage.getOriginalName());
            dto.setExtension(fileStorage.getExtension());
            dto.setContentType(fileStorage.getContentType());
            dto.setModule(fileStorage.getModule());
            dto.setNote(fileStorage.getNote());
            dto.setUploadByStr(fileStorage.getUploadBy().getUsername());
            dto.setSrc(FileUtils.getImageUrl(fileStorage, false));
            //dto.setIsActive(fileStorage.isActive());
            dto.setStatus(fileStorage.isStatus());
            dto.setSize(fileStorage.getFileSize());
            dto.setContent(fileStorage.getContent());
            dto.setUploadAt(fileStorage.getCreatedAt());
        }
        return dto;
    }

    public static List<FileDTO> toDTOs(List<FileStorage> fileStorages) {
        List<FileDTO> list = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(fileStorages)) {
            for (FileStorage f : fileStorages) {
                list.add(FileDTO.toDto(f));
            }
        }
        return list;
    }
}