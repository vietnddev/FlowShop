package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.sales.entity.VoucherTicket;
import com.flowiee.pms.modules.sales.dto.VoucherTicketDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VoucherTicketService extends ICurdService<VoucherTicketDTO> {
    Page<VoucherTicket> findAll(int pageSize, int pageNum, Long voucherId);

    List<VoucherTicket> findByVoucherId(Long voucherId);

    VoucherTicket findByCode(String code);

    VoucherTicketDTO isAvailable(String voucherTicketCode);

    VoucherTicket findTicketByCode(String code);

    String checkTicketToUse(String code);
}