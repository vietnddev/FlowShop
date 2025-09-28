package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.modules.inventory.enums.TransactionGoodsType;
import com.flowiee.pms.modules.sales.dto.OrderDTO;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.inventory.entity.Storage;
import com.flowiee.pms.modules.inventory.entity.TransactionGoodsItem;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TransactionGoodsDTO {
    private Long id;
    private String code;
    private String title;
    private String source;
    private TransactionGoodsType transactionType;
    private String transactionStatus;
    private String description;
    private LocalDateTime transactionTime;
    private String createdBy;
    private LocalDateTime createdTime;
    private String confirmedBy;
    private LocalDateTime confirmedTime;
    private String approvedBy;
    private LocalDateTime approvedTime;
    private String rejectedBy;
    private LocalDateTime rejectedTime;
    private String rejectedReason;
    private String requestNote;
    private String purpose;
    private String sourceType;
    private OrderDTO order;
    private StorageDTO warehouse;
    private List<TransactionGoodsItemDTO> items;
}