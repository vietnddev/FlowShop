package com.flowiee.pms.modules.promotion.service;

import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.promotion.dto.PromotionApplyDTO;

import java.util.List;

public interface PromotionApplyService extends BaseCurdService<PromotionApplyDTO> {
    List<PromotionApplyDTO> findAll(Long promotionId , Long productId);

    List<PromotionApplyDTO> findByPromotionId(Long promotionId);
}