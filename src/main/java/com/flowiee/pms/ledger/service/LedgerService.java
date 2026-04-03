package com.flowiee.pms.ledger.service;

import com.flowiee.pms.ledger.model.GeneralLedger;

import java.time.LocalDate;

public interface LedgerService {
    GeneralLedger findGeneralLedger(int pageSize, int pageNum, LocalDate fromDate, LocalDate toDate);
}