package com.flowiee.pms.modules.sales.service.impl;

import com.flowiee.pms.modules.sales.repository.LedgerTransactionRepository;
import com.flowiee.pms.common.enumeration.LedgerTranType;
import com.flowiee.pms.modules.sales.service.LedgerPaymentService;
import com.flowiee.pms.modules.system.service.SystemLogService;
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