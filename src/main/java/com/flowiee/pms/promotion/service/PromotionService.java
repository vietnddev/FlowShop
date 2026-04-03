package com.flowiee.pms.promotion.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.promotion.dto.PromotionInfoDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionService extends ICurdService<PromotionInfoDTO> {
    Page<PromotionInfoDTO> findAll(int pageSize, int pageNum, String pTitle, LocalDateTime pStartTime, LocalDateTime pEndTime, String pStatus);

    void notifyToCustomer(List<Long> pCustomerIdList, Long pPromotionId);
}