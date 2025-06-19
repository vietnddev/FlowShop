package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.sales.dto.VoucherApplyDTO;
import com.flowiee.pms.modules.sales.entity.VoucherApply;

import java.util.List;

public interface VoucherApplyService extends ICurdService<VoucherApply> {
    List<VoucherApplyDTO> findAll(Long voucherInfoId , Long productId);

    List<VoucherApplyDTO> findByProductId(Long productId);

    List<VoucherApply> findByVoucherId(Long voucherId);
}