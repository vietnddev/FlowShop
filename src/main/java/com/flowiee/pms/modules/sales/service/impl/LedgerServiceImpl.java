package com.flowiee.pms.modules.sales.service.impl;

import com.flowiee.pms.modules.sales.model.GeneralLedger;
import com.flowiee.pms.modules.sales.dto.LedgerTransactionDTO;
import com.flowiee.pms.modules.sales.repository.LedgerTransactionRepository;
import com.flowiee.pms.modules.sales.service.LedgerService;
import com.flowiee.pms.modules.sales.service.LedgerTransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LedgerServiceImpl implements LedgerService {
    LedgerTransactionRepository mvLedgerTransactionRepository;
    @Autowired
    @NonFinal
    @Qualifier("ledgerTransactionServiceImpl")
    LedgerTransactionService mvLedgerTransactionService;

    @Override
    public GeneralLedger findGeneralLedger(int pageSize, int pageNum, LocalDate pFromDate, LocalDate pToDate) {
        LocalDate currentMonth = LocalDate.now();
        if (ObjectUtils.isEmpty(pFromDate)) {
            pFromDate = currentMonth.withDayOfMonth(1);
        }
        if (ObjectUtils.isEmpty(pToDate)) {
            pToDate = currentMonth.withDayOfMonth(currentMonth.lengthOfMonth());
        }
        LocalDateTime fromDate = LocalDateTime.of(pFromDate, LocalTime.of(0, 0, 0, 0));
        LocalDateTime toDate = LocalDateTime.of(pToDate, LocalTime.of(23, 59, 59, 999999999));
        Page<LedgerTransactionDTO> ledgerTransactions = mvLedgerTransactionService.findAll(pageSize, pageNum, LocalDate.now(), LocalDate.now());

        LocalDateTime fromDateBeginBal = fromDate.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        BigDecimal beginBal = mvLedgerTransactionRepository.calBeginBalance(fromDateBeginBal);
        BigDecimal[] totalReceiptPayment = mvLedgerTransactionRepository.calTotalReceiptAndTotalPayment(fromDate, toDate).get(0);
        BigDecimal totalReceipt = totalReceiptPayment[0];
        BigDecimal totalPayment = totalReceiptPayment[1];
        BigDecimal endBal = beginBal.add(totalReceipt).subtract(totalPayment);

        return GeneralLedger.builder()
                .beginBalance(beginBal)
                .totalReceipt(totalReceipt)
                .totalPayment(totalPayment)
                .endBalance(endBal)
                .listTransactions(ledgerTransactions.getContent())
                .totalPages(ledgerTransactions.getTotalPages())
                .totalElements(ledgerTransactions.getTotalElements())
                .build();
    }
}