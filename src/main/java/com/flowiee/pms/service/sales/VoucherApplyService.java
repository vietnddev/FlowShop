package com.flowiee.pms.service.sales;

import com.flowiee.pms.base.service.BaseCurdService;
import com.flowiee.pms.model.dto.VoucherApplyDTO;
import com.flowiee.pms.entity.sales.VoucherApply;

import java.util.List;

public interface VoucherApplyService extends BaseCurdService<VoucherApply> {
    List<VoucherApplyDTO> findAll(Long voucherInfoId , Long productId);

    List<VoucherApplyDTO> findByProductId(Long productId);

    List<VoucherApply> findByVoucherId(Long voucherId);
}