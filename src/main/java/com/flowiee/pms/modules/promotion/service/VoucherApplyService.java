package com.flowiee.pms.modules.promotion.service;

import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.promotion.dto.VoucherApplyDTO;
import com.flowiee.pms.modules.promotion.entity.VoucherApply;

import java.util.List;

public interface VoucherApplyService extends BaseCurdService<VoucherApply> {
    List<VoucherApplyDTO> findAll(Long voucherInfoId , Long productId);

    List<VoucherApplyDTO> findByProductId(Long productId);

    List<VoucherApply> findByVoucherId(Long voucherId);
}