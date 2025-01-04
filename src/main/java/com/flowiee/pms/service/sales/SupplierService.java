package com.flowiee.pms.service.sales;

import com.flowiee.pms.base.service.BaseCurdService;
import com.flowiee.pms.entity.sales.Supplier;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SupplierService extends BaseCurdService<Supplier> {
    Page<Supplier> findAll(Integer pageSize, Integer pageNum, List<Long> igroneIds);
}