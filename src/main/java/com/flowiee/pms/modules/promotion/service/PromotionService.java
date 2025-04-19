package com.flowiee.pms.modules.promotion.service;

import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.promotion.dto.PromotionInfoDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionService extends BaseCurdService<PromotionInfoDTO> {
    Page<PromotionInfoDTO> findAll(int pageSize, int pageNum, String pTitle, LocalDateTime pStartTime, LocalDateTime pEndTime, String pStatus);

    void notifyToCustomer(List<Long> pCustomerIdList, Long pPromotionId);
}