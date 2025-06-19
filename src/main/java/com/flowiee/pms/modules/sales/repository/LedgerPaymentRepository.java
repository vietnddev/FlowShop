package com.flowiee.pms.modules.sales.repository;

import com.flowiee.pms.modules.sales.entity.LedgerPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerPaymentRepository extends JpaRepository<LedgerPayment, Long> {
    @Query(value = "select payment_index from ledger_payment order by id desc fetch first 1 rows only", nativeQuery = true)
    Long findLastIndex();
}