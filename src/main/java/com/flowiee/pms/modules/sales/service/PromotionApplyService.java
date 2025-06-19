package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.sales.dto.PromotionApplyDTO;

import java.util.List;

public interface PromotionApplyService extends ICurdService<PromotionApplyDTO> {
    List<PromotionApplyDTO> findAll(Long promotionId , Long productId);

    List<PromotionApplyDTO> findByPromotionId(Long promotionId);
}