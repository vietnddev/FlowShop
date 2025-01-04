package com.flowiee.pms.service.sales;

import com.flowiee.pms.base.service.BaseCurdService;
import com.flowiee.pms.model.dto.PromotionInfoDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionService extends BaseCurdService<PromotionInfoDTO> {
    Page<PromotionInfoDTO> findAll(int pageSize, int pageNum, String pTitle, LocalDateTime pStartTime, LocalDateTime pEndTime, String pStatus);

    void notifyToCustomer(List<Long> pCustomerIdList, Long pPromotionId);
}