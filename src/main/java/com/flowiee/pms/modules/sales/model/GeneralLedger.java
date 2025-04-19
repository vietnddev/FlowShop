package com.flowiee.pms.modules.sales.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowiee.pms.modules.sales.dto.LedgerTransactionDTO;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeneralLedger {
    BigDecimal beginBalance;
    BigDecimal totalReceipt;
    BigDecimal totalPayment;
    BigDecimal endBalance;
    List<LedgerTransactionDTO> listTransactions;

    @JsonIgnore
    int totalPages;
    @JsonIgnore
    long totalElements;
}