package com.flowiee.pms.modules.inventory.model;

import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.modules.inventory.dto.StorageDTO;
import com.flowiee.pms.modules.inventory.dto.TransactionGoodsItemDTO;
import com.flowiee.pms.modules.sales.dto.OrderDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class TransactionGoodsReq extends BaseParameter {
    private String code;
    private String source;
    private String type;
    private String status;
    private String description;
    private LocalDateTime transactionTime;
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
    private List<TransactionGoodsItemDTO> dtoItems;
    private List<TransactionGoodsItemReq> reqItems;
}