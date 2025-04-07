package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.base.service.BaseGService;
import com.flowiee.pms.entity.sales.LedgerTransaction;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.model.dto.LedgerTransactionDTO;
import com.flowiee.pms.repository.sales.LedgerTransactionRepository;
import com.flowiee.pms.service.sales.LedgerTransactionService;
import com.flowiee.pms.common.enumeration.LedgerTranStatus;
import com.flowiee.pms.common.enumeration.MasterObject;
import com.flowiee.pms.service.system.SystemLogService;
import org.apache.commons.lang3.ObjectUtils;;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LedgerTransactionServiceImpl extends BaseGService<LedgerTransaction, LedgerTransactionDTO, LedgerTransactionRepository>
        implements LedgerTransactionService {
    private final SystemLogService mvSystemLogService;

    public LedgerTransactionServiceImpl(LedgerTransactionRepository pEntityRepository, SystemLogService pSystemLogService) {
        super(LedgerTransaction.class, LedgerTransactionDTO.class, pEntityRepository);
        this.mvSystemLogService = pSystemLogService;
    }

    @Override
    public List<LedgerTransactionDTO> findAll() {
        return this.findAll(-1, -1, null, null).getContent();
    }

    @Override
    public Page<LedgerTransactionDTO> findAll(int pageSize, int pageNum, LocalDate fromDate, LocalDate toDate) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("createdAt").descending());
        Page<LedgerTransaction> lvLedgerTransactions = mvEntityRepository.findAll(getTranType(), null, null, pageable);
        for (LedgerTransaction trans : lvLedgerTransactions) {
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
        return new PageImpl<>(convertDTOs(lvLedgerTransactions.getContent()), pageable, lvLedgerTransactions.getTotalElements());
    }

    @Override
    public LedgerTransactionDTO findById(Long pTranId, boolean pThrowException) {
        return super.findById(pTranId, pThrowException);
    }

    @Override
    public LedgerTransactionDTO save(LedgerTransactionDTO pTransaction) {
        Long lastIndex = mvEntityRepository.findLastIndex(getTranType());
        if (ObjectUtils.isEmpty(lastIndex)) {
            lastIndex = 1l;
        } else {
            lastIndex += 1l;
        }
        if (ObjectUtils.isEmpty(pTransaction.getTranCode())) {
            pTransaction.setTranCode(getNextTranCode(lastIndex));
        }
        pTransaction.setTranType(getTranType());
        pTransaction.setTranIndex(lastIndex);
        pTransaction.setStatus(LedgerTranStatus.COMPLETED.name());
        LedgerTransactionDTO transactionSaved = super.save(pTransaction);

        String logTitle = "Thêm mới phiếu thu";
        ACTION logFunc = ACTION.SLS_RCT_C;
        if (transactionSaved.getTranType().equals("PC")) {
            logTitle = "Thêm mới phiếu chi";
            logFunc = ACTION.SLS_PMT_C;
        }
        mvSystemLogService.writeLogCreate(MODULE.PRODUCT, logFunc, MasterObject.LedgerTransaction, logTitle, transactionSaved.getGroupObject().getName());

        return transactionSaved;
    }

    @Override
    public LedgerTransactionDTO update(LedgerTransactionDTO transaction, Long pTranId) {
        LedgerTransaction lvLedgerTransaction = super.findById(pTranId).orElseThrow(() -> new BadRequestException());

        lvLedgerTransaction.setTranType(getTranType());
        LedgerTransaction lvTransactionUpdated = mvEntityRepository.save(lvLedgerTransaction);

        String logTitle = "Cập nhật phiếu thu";
        ACTION logFunc = ACTION.SLS_RCT_U;
        if (lvTransactionUpdated.getTranType().equals("PC")) {
            logTitle = "Cập nhật phiếu chi";
            logFunc = ACTION.SLS_PMT_U;
        }
        mvSystemLogService.writeLogCreate(MODULE.PRODUCT, logFunc, MasterObject.LedgerTransaction, logTitle, lvTransactionUpdated.getGroupObject().getName());

        return convertDTO(lvTransactionUpdated);
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