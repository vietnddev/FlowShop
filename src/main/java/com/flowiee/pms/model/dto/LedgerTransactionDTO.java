package com.flowiee.pms.model.dto;

import com.flowiee.pms.base.service.BaseDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class LedgerTransactionDTO extends BaseDTO implements Serializable {
    Long tranIndex;
    String tranCode;
    String tranType;
    CategoryDTO groupObject;
    CategoryDTO tranContent;
    CategoryDTO paymentMethod;
    String fromToName;
    BigDecimal amount;
    String description;
    String status;
    String tranContentName;
    String groupObjectName;
    String paymentMethodName;
}