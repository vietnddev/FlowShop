package com.flowiee.pms.model.dto;

import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.entity.storage.Storage;
import com.flowiee.pms.entity.storage.TransactionGoodsItem;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TransactionGoodsDTO {
    private Long id;
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
    private Order order;
    private Storage warehouse;
    private List<TransactionGoodsItem> items;
}