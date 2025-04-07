package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.repository.sales.LedgerTransactionRepository;
import com.flowiee.pms.service.sales.LedgerPaymentService;
import com.flowiee.pms.common.enumeration.LedgerTranType;
import com.flowiee.pms.service.system.SystemLogService;
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