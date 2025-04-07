package com.flowiee.pms.service.sales;

import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.model.dto.LedgerTransactionDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface LedgerTransactionService extends BaseCurdService<LedgerTransactionDTO> {
    Page<LedgerTransactionDTO> findAll(int pageSize, int pageNum, LocalDate fromDate, LocalDate toDate);
}