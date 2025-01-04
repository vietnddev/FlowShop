package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.entity.sales.LedgerTransaction;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.repository.sales.LedgerTransactionRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.sales.LedgerTransactionService;
import com.flowiee.pms.common.enumeration.LedgerTranStatus;
import com.flowiee.pms.common.enumeration.MasterObject;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LedgerTransactionServiceImpl extends BaseService implements LedgerTransactionService {
    @Autowired
    LedgerTransactionRepository mvLedgerTransactionRepository;

    @Override
    public List<LedgerTransaction> findAll() {
        return this.findAll(-1, -1, null, null).getContent();
    }

    @Override
    public Page<LedgerTransaction> findAll(int pageSize, int pageNum, LocalDate fromDate, LocalDate toDate) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("createdAt").descending());
        Page<LedgerTransaction> ledgerTransactions = mvLedgerTransactionRepository.findAll(getTranType(), null, null, pageable);
        for (LedgerTransaction trans : ledgerTransactions) {
            for (LedgerTranStatus transStatus : LedgerTranStatus.values()) {
                if (transStatus.name().equals(trans.getStatus())) {
                    trans.setStatus(transStatus.getDescription());
                }
            }
            if (trans.getGroupObject() != null) {
                trans.setGroupObjectName(trans.getGroupObject().getName());
            }
            if (trans.getTranContent() != null) {
                trans.setTranContentName(trans.getTranContent().getName());
            }
        }
        return ledgerTransactions;
    }

    @Override
    public LedgerTransaction findById(Long tranId, boolean pThrowException) {
        Optional<LedgerTransaction> entityOptional = mvLedgerTransactionRepository.findById(tranId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"ledger transaction"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public LedgerTransaction save(LedgerTransaction transaction) {
        Long lastIndex = mvLedgerTransactionRepository.findLastIndex(getTranType());
        if (ObjectUtils.isEmpty(lastIndex)) {
            lastIndex = 1l;
        } else {
            lastIndex += 1l;
        }
        if (ObjectUtils.isEmpty(transaction.getTranCode())) {
            transaction.setTranCode(getNextTranCode(lastIndex));
        }
        transaction.setTranType(getTranType());
        transaction.setTranIndex(lastIndex);
        transaction.setStatus(LedgerTranStatus.COMPLETED.name());
        LedgerTransaction transactionSaved = mvLedgerTransactionRepository.save(transaction);

        String logTitle = "Thêm mới phiếu thu";
        ACTION logFunc = ACTION.SLS_RCT_C;
        if (transactionSaved.getTranType().equals("PC")) {
            logTitle = "Thêm mới phiếu chi";
            logFunc = ACTION.SLS_PMT_C;
        }
        systemLogService.writeLogCreate(MODULE.PRODUCT, logFunc, MasterObject.LedgerTransaction, logTitle, transactionSaved.getGroupObject().getName());

        return transactionSaved;
    }

    @Override
    public LedgerTransaction update(LedgerTransaction transaction, Long tranId) {
        transaction.setId(tranId);
        transaction.setTranType(getTranType());
        LedgerTransaction transactionUpdated = mvLedgerTransactionRepository.save(transaction);

        String logTitle = "Cập nhật phiếu thu";
        ACTION logFunc = ACTION.SLS_RCT_U;
        if (transactionUpdated.getTranType().equals("PC")) {
            logTitle = "Cập nhật phiếu chi";
            logFunc = ACTION.SLS_PMT_U;
        }
        systemLogService.writeLogCreate(MODULE.PRODUCT, logFunc, MasterObject.LedgerTransaction, logTitle, transactionUpdated.getGroupObject().getName());

        return transactionUpdated;
    }

    @Override
    public String delete(Long tranId) {
        throw new AppException("Method dose not support!");
    }

    protected String getTranType() {
        return null;
    }

    public String getNextTranCode(long lastIndex) {
        return getTranType() + String.format("%05d", lastIndex);
    }
}