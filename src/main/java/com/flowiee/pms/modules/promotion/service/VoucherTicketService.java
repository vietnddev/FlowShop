package com.flowiee.pms.modules.promotion.service;

import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.promotion.entity.VoucherTicket;
import com.flowiee.pms.modules.promotion.dto.VoucherTicketDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VoucherTicketService extends BaseCurdService<VoucherTicket> {
    Page<VoucherTicket> findAll(int pageSize, int pageNum, Long voucherId);

    List<VoucherTicket> findByVoucherId(Long voucherId);

    VoucherTicket findByCode(String code);

    VoucherTicketDTO isAvailable(String voucherTicketCode);

    VoucherTicket findTicketByCode(String code);

    String checkTicketToUse(String code);
}