package com.flowiee.pms.modules.sales.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.sales.entity.LedgerTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LedgerTransactionRepository extends BaseRepository<LedgerTransaction, Long> {
    @Query("from LedgerTransaction t " +
           "where 1=1 " +
           "and (:type is null or t.tranType=:type) " +
           "and ((:fromDate is null and :toDate is null) or (:fromDate <= t.createdAt and :toDate >= t.createdAt))")
    Page<LedgerTransaction> findAll(@Param("type") String type, @Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate, Pageable pageable);

    @Query(value = "select tran_index from ledger_transaction where tran_type = :type order by id desc fetch first 1 rows only", nativeQuery = true)
    Long findLastIndex(@Param("type") String type);

    @Query("select coalesce(sum((case when t.tranType = 'PT' then t.amount else 0 end) - (case when t.tranType = 'PC' then t.amount else 0 end)), 0) as beginBal " +
           "from LedgerTransaction t " +
           "where t.createdAt <= :fromDate")
    BigDecimal calBeginBalance(@Param("fromDate") LocalDateTime fromDate);

    @Query("select " +
           "    coalesce(sum(case when t.tranType = 'PT' then t.amount else 0 end), 0) as total_receipt, " +
           "    coalesce(sum(case when t.tranType = 'PC' then t.amount else 0 end), 0) as total_payment " +
           "from LedgerTransaction t " +
           "where t.createdAt >= :fromDate and t.createdAt <= :toDate")
    List<BigDecimal[]> calTotalReceiptAndTotalPayment(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}