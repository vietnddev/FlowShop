package com.flowiee.pms.ledger.service;

import com.flowiee.pms.shared.base.CreateService;
import com.flowiee.pms.shared.base.FindService;
import com.flowiee.pms.ledger.dto.LedgerTransactionDTO;
import com.flowiee.pms.shared.base.UpdateService;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface LedgerTransactionService extends FindService<LedgerTransactionDTO>, CreateService<LedgerTransactionDTO>,
        UpdateService<LedgerTransactionDTO> {
    Page<LedgerTransactionDTO> findAll(int pageSize, int pageNum, LocalDate fromDate, LocalDate toDate);
}