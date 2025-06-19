package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.sales.dto.PromotionInfoDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionService extends ICurdService<PromotionInfoDTO> {
    Page<PromotionInfoDTO> findAll(int pageSize, int pageNum, String pTitle, LocalDateTime pStartTime, LocalDateTime pEndTime, String pStatus);

    void notifyToCustomer(List<Long> pCustomerIdList, Long pPromotionId);
}