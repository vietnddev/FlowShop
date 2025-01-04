package com.flowiee.pms.service.sales;

import com.flowiee.pms.base.service.BaseCurdService;
import com.flowiee.pms.model.dto.PromotionApplyDTO;

import java.util.List;

public interface PromotionApplyService extends BaseCurdService<PromotionApplyDTO> {
    List<PromotionApplyDTO> findAll(Long promotionId , Long productId);

    List<PromotionApplyDTO> findByPromotionId(Long promotionId);
}