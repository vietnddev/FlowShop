package com.flowiee.pms.ledger.service.impl;

import com.flowiee.pms.ledger.repository.LedgerTransactionRepository;
import com.flowiee.pms.ledger.enums.LedgerTranType;
import com.flowiee.pms.ledger.service.LedgerReceiptService;
import com.flowiee.pms.system.service.SystemLogService;
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