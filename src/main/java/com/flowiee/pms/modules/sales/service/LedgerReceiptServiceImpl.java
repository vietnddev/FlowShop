package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.modules.sales.repository.LedgerTransactionRepository;
import com.flowiee.pms.common.enumeration.LedgerTranType;
import com.flowiee.pms.modules.log.service.SystemLogService;
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