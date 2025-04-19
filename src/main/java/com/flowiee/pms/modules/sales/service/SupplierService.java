package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.sales.dto.SupplierDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SupplierService extends BaseCurdService<SupplierDTO> {
    Page<SupplierDTO> findAll(Integer pageSize, Integer pageNum, List<Long> igroneIds);
}