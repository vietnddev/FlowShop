package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.service.sales.LedgerPaymentService;
import com.flowiee.pms.common.enumeration.LedgerTranType;
import org.springframework.stereotype.Service;

@Service
public class LedgerPaymentServiceImpl extends LedgerTransactionServiceImpl implements LedgerPaymentService {
    @Override
    protected String getTranType() {
        return LedgerTranType.PC.name();
    }
}