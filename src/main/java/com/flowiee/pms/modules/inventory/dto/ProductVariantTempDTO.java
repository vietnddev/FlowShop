package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.modules.inventory.entity.ProductVariantExim;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantTempDTO extends ProductVariantExim implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    String variantCode;
    Long ticketImportId;
    Long ticketExportId;
    Long productVariantId;
    String productVariantName;
    //Popup's field get import/export storage of product
    String staff;
    String changeQty;
    String branchName;

    public static ProductVariantTempDTO convertToDTO(ProductVariantExim inputEntity) {
        ProductVariantTempDTO outputDTO = new ProductVariantTempDTO();
        outputDTO.setId(inputEntity.getId());
        outputDTO.setProductVariant(inputEntity.getProductVariant());
        outputDTO.setTicketImport(inputEntity.getTicketImport());
        outputDTO.setTicketExport(inputEntity.getTicketExport());
        outputDTO.setPurchasePrice(inputEntity.getPurchasePrice());
        outputDTO.setQuantity(inputEntity.getQuantity());
        outputDTO.setNote(inputEntity.getNote());
        outputDTO.setProductVariantId(inputEntity.getProductVariant().getId());
        outputDTO.setProductVariantName(inputEntity.getProductVariant().getVariantName());
        if (outputDTO.getProductVariant() != null) {
            outputDTO.setVariantCode(outputDTO.getProductVariant().getVariantCode());
            outputDTO.setProductVariantId(outputDTO.getProductVariant().getId());
            outputDTO.setProductVariantName(outputDTO.getProductVariant().getVariantName());
        }
        if (outputDTO.getTicketImport() != null) {
            outputDTO.setTicketImportId(inputEntity.getTicketImport().getId());
        }
        if (outputDTO.getTicketExport() != null) {
            outputDTO.setTicketExportId(inputEntity.getTicketExport().getId());
        }
        outputDTO.setStorageQty(inputEntity.getStorageQty());
        outputDTO.setCreatedAt(inputEntity.getCreatedAt());
        outputDTO.setAction(inputEntity.getAction());
        return outputDTO;
    }

    public static List<ProductVariantTempDTO> convertToDTOs(List<ProductVariantExim> inputEntities) {
        List<ProductVariantTempDTO> outputDTOs = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(inputEntities)) {
            for (ProductVariantExim dto : inputEntities) {
                outputDTOs.add(convertToDTO(dto));
            }
        }
        return outputDTOs;
    }
}