package com.flowiee.pms.model.dto;

import com.flowiee.pms.entity.storage.Storage;
import com.flowiee.pms.model.StorageItems;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StorageDTO implements Serializable {
	@Serial
	static final long serialVersionUID = 1L;

	Long id;
    String name;
    String code;
    String location;
    Double area;
    Integer holdableQty;
    Integer holdWarningPercent;
    String description;
    Boolean isDefault;
    String status;

	Integer totalItems;
    BigDecimal totalInventoryValue;
    List<TicketImportDTO> listTicketImportDTO;
    List<ProductVariantDTO> listProductVariantDTO;
    List<MaterialDTO> listMaterialDTO;
    List<StorageItems> listStorageItems;

    public static StorageDTO convertToDTO(Storage inputEntity) {
        if (inputEntity == null) {
            return null;
        }
        StorageDTO outputDTO = new StorageDTO();
        outputDTO.setId(inputEntity.getId());
        outputDTO.setName(inputEntity.getName());
        outputDTO.setCode(inputEntity.getCode());
        outputDTO.setLocation(inputEntity.getLocation());
        outputDTO.setArea(inputEntity.getArea());
        outputDTO.setHoldableQty(inputEntity.getHoldableQty());
        outputDTO.setHoldWarningPercent(inputEntity.getHoldWarningPercent());
        outputDTO.setDescription(inputEntity.getDescription());
        outputDTO.setIsDefault(inputEntity.getIsDefault());
        outputDTO.setStatus(inputEntity.getStatus());
        return outputDTO;
    }

    public static List<StorageDTO> convertToDTOs(List<Storage> inputEntities) {
        List<StorageDTO> outputDTOs = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(inputEntities)) {
            for (Storage s : inputEntities) {
                outputDTOs.add(StorageDTO.convertToDTO(s));
            }
        }
        return outputDTOs;
    }
}