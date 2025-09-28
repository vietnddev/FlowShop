package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.modules.inventory.entity.Storage;
import com.flowiee.pms.modules.inventory.model.StorageItems;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StorageDTO implements Serializable {
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
    List<TransactionGoodsDTO> transactionGoodsImportList;
    List<ProductVariantDTO> listProductVariantDTO;
    List<MaterialDTO> listMaterialDTO;
    List<StorageItems> listStorageItems;

    public StorageDTO(Long id) {
        this.id = id;
    }

    public static StorageDTO toDto(Storage inputEntity) {
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
                outputDTOs.add(StorageDTO.toDto(s));
            }
        }
        return outputDTOs;
    }
}