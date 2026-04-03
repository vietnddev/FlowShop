package com.flowiee.pms.promotion.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.promotion.dto.PromotionApplyDTO;

import java.util.List;

public interface PromotionApplyService extends ICurdService<PromotionApplyDTO> {
    List<PromotionApplyDTO> findAll(Long promotionId , Long productId);

    List<PromotionApplyDTO> findByPromotionId(Long promotionId);
}