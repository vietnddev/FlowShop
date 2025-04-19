package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.modules.sales.model.GeneralLedger;

import java.time.LocalDate;

public interface LedgerService {
    GeneralLedger findGeneralLedger(int pageSize, int pageNum, LocalDate fromDate, LocalDate toDate);
}