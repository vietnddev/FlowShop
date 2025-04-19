package com.flowiee.pms.modules.promotion.repository;

import com.flowiee.pms.modules.promotion.entity.VoucherTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherTicketRepository extends JpaRepository<VoucherTicket, Long> {
    @Query("from VoucherTicket v where v.voucherInfo.id=:voucherId")
    Page<VoucherTicket> findByVoucherId(@Param("voucherId") Long voucherId, Pageable pageable);
    
    @Query("from VoucherTicket v where v.code=:code")
    VoucherTicket findByCode(@Param("code") String code);
}