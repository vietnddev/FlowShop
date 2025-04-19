package com.flowiee.pms.modules.promotion.service;

import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.promotion.dto.VoucherInfoDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface VoucherService extends BaseCurdService<VoucherInfoDTO> {
    Page<VoucherInfoDTO> findAll(int pageSize, int pageNum, List<Long> pIds, String pTitle, LocalDateTime pStartTime, LocalDateTime pEndTime, String pStatus);
}