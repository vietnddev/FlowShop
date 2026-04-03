package com.flowiee.pms.supplier.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.supplier.dto.SupplierDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SupplierService extends ICurdService<SupplierDTO> {
    Page<SupplierDTO> findAll(Integer pageSize, Integer pageNum, List<Long> igroneIds);
}