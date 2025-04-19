package com.flowiee.pms.modules.sales.repository;

import com.flowiee.pms.modules.sales.entity.LedgerReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerReceiptRepository extends JpaRepository<LedgerReceipt, Long> {
    @Query(value = "select receipt_index from ledger_receipt order by id desc fetch first 1 rows only", nativeQuery = true)
    Long findLastIndex();
}