package com.flowiee.pms.promotion.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.promotion.dto.VoucherApplyDTO;
import com.flowiee.pms.promotion.entity.VoucherApply;

import java.util.List;

public interface VoucherApplyService extends ICurdService<VoucherApply> {
    List<VoucherApplyDTO> findAll(Long voucherInfoId , Long productId);

    List<VoucherApplyDTO> findByProductId(Long productId);

    List<VoucherApply> findByVoucherId(Long voucherId);
}