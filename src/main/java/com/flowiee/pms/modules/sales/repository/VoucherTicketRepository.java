package com.flowiee.pms.modules.sales.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.sales.entity.VoucherTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherTicketRepository extends BaseRepository<VoucherTicket, Long> {
    @Query("from VoucherTicket v where v.voucherInfo.id=:voucherId")
    Page<VoucherTicket> findByVoucherId(@Param("voucherId") Long voucherId, Pageable pageable);
    
    @Query("from VoucherTicket v where v.code=:code")
    VoucherTicket findByCode(@Param("code") String code);
}