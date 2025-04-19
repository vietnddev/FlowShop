package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.sales.dto.LedgerTransactionDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface LedgerTransactionService extends BaseCurdService<LedgerTransactionDTO> {
    Page<LedgerTransactionDTO> findAll(int pageSize, int pageNum, LocalDate fromDate, LocalDate toDate);
}