package com.flowiee.pms.modules.sales.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.modules.system.dto.CategoryDTO;
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