package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.modules.inventory.entity.Storage;
import com.flowiee.pms.modules.inventory.entity.TransactionGoods;
import com.flowiee.pms.modules.inventory.entity.TransactionGoodsItem;
import com.flowiee.pms.modules.inventory.enums.TransactionGoodsStatus;
import com.flowiee.pms.modules.inventory.enums.TransactionGoodsType;
import com.flowiee.pms.modules.sales.dto.OrderDTO;
import com.flowiee.pms.modules.sales.entity.Order;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionGoodsDTO {
    private Long id;
    private String code;
    private String title;
    private String source;
    private TransactionGoodsType transactionType;
    private TransactionGoodsStatus transactionStatus;
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

    public static TransactionGoodsDTO toDto(TransactionGoods pInput) {
        return TransactionGoodsDTO.builder()
                .id(pInput.getId())
                .code(pInput.getCode())
                .title(pInput.getTitle())
                .source(pInput.getSource())
                .transactionType(pInput.getTransactionType())
                .transactionStatus(pInput.getTransactionStatus())
                .description(pInput.getDescription())
                .transactionTime(pInput.getTransactionTime())
                .createdBy(pInput.getCreatedBy() + "")
                .createdTime(pInput.getCreatedAt())
                .confirmedBy(pInput.getConfirmedBy())
                .confirmedTime(pInput.getConfirmedTime())
                .approvedBy(pInput.getApprovedBy())
                .approvedTime(pInput.getApprovedTime())
                .rejectedBy(pInput.getRejectedBy())
                .rejectedTime(pInput.getRejectedTime())
                .rejectedReason(pInput.getRejectedReason())
                .requestNote(pInput.getRequestNote())
                .purpose(pInput.getPurpose())
                .sourceType(pInput.getSourceType())
                .order(mapOrder(pInput.getOrder()))
                .warehouse(mapStorage(pInput.getWarehouse()))
                .items(mapItems(pInput.getItems()))
                .build();
    }

    public static List<TransactionGoodsDTO> toDTOs(List<TransactionGoods> inputEntities) {
        List<TransactionGoodsDTO> outputDTOs = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(inputEntities)) {
            for (TransactionGoods s : inputEntities) {
                outputDTOs.add(TransactionGoodsDTO.toDto(s));
            }
        }
        return outputDTOs;
    }

    private static OrderDTO mapOrder(Order pInput) {
        if (pInput == null) {
            return new OrderDTO();
        }
        return OrderDTO.toDto(pInput);
    }

    private static StorageDTO mapStorage(Storage pInput) {
        if (pInput == null) {
            return new StorageDTO();
        }
        return StorageDTO.toDto(pInput);
    }

    private static List<TransactionGoodsItemDTO> mapItems(List<TransactionGoodsItem> pInput) {
        if (CollectionUtils.isEmpty(pInput)) {
            return List.of();
        }
        return TransactionGoodsItemDTO.toDTOs(pInput);
    }
}