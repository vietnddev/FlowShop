package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.sales.dto.LedgerTransactionDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface LedgerTransactionService extends ICurdService<LedgerTransactionDTO> {
    Page<LedgerTransactionDTO> findAll(int pageSize, int pageNum, LocalDate fromDate, LocalDate toDate);
}