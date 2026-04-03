package com.flowiee.pms.ledger.dto;

import com.flowiee.pms.shared.base.BaseDTO;
import com.flowiee.pms.system.dto.CategoryDTO;
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
    //String tranContentName;
    //String groupObjectName;
    //String paymentMethodName;
}