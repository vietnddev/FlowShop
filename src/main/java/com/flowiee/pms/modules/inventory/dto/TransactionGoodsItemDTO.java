package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.modules.inventory.entity.Material;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.inventory.entity.TransactionGoodsItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TransactionGoodsItemDTO extends BaseDTO {
    private TransactionGoodsDTO transactionGoods;
    private ProductVariantDTO productVariant;
    private MaterialDTO material;
    private String itemType;
    private String itemName;
    private BigDecimal unitCost;
    private Integer quantity;
    private BigDecimal amount;
    private String note;

    public static TransactionGoodsItemDTO toDto(TransactionGoodsItem pInput) {
        ProductDetail lvProductVariant = pInput.getProductVariant();
        Material lvMaterial = pInput.getMaterial();

        String itemType = lvProductVariant != null ? "Product" : (lvMaterial != null ? "Material" : "Unknown type");
        String itemName = lvProductVariant != null ? lvProductVariant.getVariantName() : (lvMaterial != null ? lvMaterial.getName() : "Unknown name");

        TransactionGoodsItemDTO lvDto = new TransactionGoodsItemDTO();
        lvDto.setTransactionGoods(TransactionGoodsDTO.builder().id(pInput.getId()).build());
        lvDto.setProductVariant(mapProductVariant(lvProductVariant));
        lvDto.setMaterial(mapMaterial(lvMaterial));
        lvDto.setItemType(itemType);
        lvDto.setItemName(itemName);
        lvDto.setUnitCost(CoreUtils.coalesce(pInput.getUnitCost(), BigDecimal.ZERO));
        lvDto.setQuantity(pInput.getQuantity());
        lvDto.setAmount(CoreUtils.coalesce(pInput.getAmount(), BigDecimal.ZERO));
        lvDto.setNote(pInput.getNote() != null ? pInput.getNote() : "-");
        return lvDto;
    }

    public static List<TransactionGoodsItemDTO> toDTOs(List<TransactionGoodsItem> inputEntities) {
        List<TransactionGoodsItemDTO> outputDTOs = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(inputEntities)) {
            for (TransactionGoodsItem s : inputEntities) {
                outputDTOs.add(TransactionGoodsItemDTO.toDto(s));
            }
        }
        return outputDTOs;
    }

    private static ProductVariantDTO mapProductVariant(ProductDetail pInput) {
        ProductVariantDTO lvProductVariant = new ProductVariantDTO();
        if (pInput == null) {
            return lvProductVariant;
        }
        lvProductVariant.setId(pInput.getId());
        return lvProductVariant;
    }

    private static MaterialDTO mapMaterial(Material pInput) {
        MaterialDTO lvMaterial = new MaterialDTO();
        if (pInput == null) {
            return lvMaterial;
        }
        lvMaterial.setId(pInput.getId());
        return lvMaterial;
    }
}