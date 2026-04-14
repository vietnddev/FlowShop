package com.flowiee.pms.promotion.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.promotion.entity.VoucherTicket;
import com.flowiee.pms.promotion.dto.VoucherTicketDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VoucherTicketService extends ICurdService<VoucherTicketDTO> {
    Page<VoucherTicket> findAll(int pageSize, int pageNum, Long voucherId);

    List<VoucherTicket> findByVoucherId(Long voucherId);

    VoucherTicket findByCode(String code);

    VoucherTicketDTO isAvailable(String voucherTicketCode);

    VoucherTicket findTicketByCode(String code);

    String checkTicketToUse(String code);

    void markCouponAsUsed(String pCouponCode, Long pCustomerId);
}