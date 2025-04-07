package com.flowiee.pms.service.sales;

import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.model.dto.SupplierDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SupplierService extends BaseCurdService<SupplierDTO> {
    Page<SupplierDTO> findAll(Integer pageSize, Integer pageNum, List<Long> igroneIds);
}