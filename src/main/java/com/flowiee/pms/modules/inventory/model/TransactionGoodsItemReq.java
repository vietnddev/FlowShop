package com.flowiee.pms.modules.inventory.model;

import lombok.Data;

@Data
public class TransactionGoodsItemReq {
    private Long productVariantId;
    private Integer quantity;
}