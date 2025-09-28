package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionGoodsItemDTO extends BaseDTO {
    private TransactionGoodsDTO transactionGoods;
    private ProductVariantDTO productVariant;
    private MaterialDTO material;
    private BigDecimal unitCost;
    private Integer quantity;
    private BigDecimal amount;
    private String note;
}