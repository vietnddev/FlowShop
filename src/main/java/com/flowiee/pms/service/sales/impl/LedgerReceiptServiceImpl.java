package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.repository.sales.LedgerTransactionRepository;
import com.flowiee.pms.service.sales.LedgerReceiptService;
import com.flowiee.pms.common.enumeration.LedgerTranType;
import com.flowiee.pms.service.system.SystemLogService;
import org.springframework.stereotype.Service;

@Service
public class LedgerReceiptServiceImpl extends LedgerTransactionServiceImpl implements LedgerReceiptService {
    public LedgerReceiptServiceImpl(LedgerTransactionRepository pEntityRepository, SystemLogService pSystemLogService) {
        super(pEntityRepository, pSystemLogService);
    }

    @Override
    protected String getTranType() {
        return LedgerTranType.PT.name();
    }
}