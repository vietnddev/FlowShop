package com.flowiee.pms.ledger.service.impl;

import com.flowiee.pms.ledger.repository.LedgerTransactionRepository;
import com.flowiee.pms.ledger.enums.LedgerTranType;
import com.flowiee.pms.ledger.service.LedgerPaymentService;
import com.flowiee.pms.system.service.SystemLogService;
import org.springframework.stereotype.Service;

@Service
public class LedgerPaymentServiceImpl extends LedgerTransactionServiceImpl implements LedgerPaymentService {
    public LedgerPaymentServiceImpl(LedgerTransactionRepository pEntityRepository, SystemLogService pSystemLogService) {
        super(pEntityRepository, pSystemLogService);
    }

    @Override
    protected String getTranType() {
        return LedgerTranType.PC.name();
    }
}